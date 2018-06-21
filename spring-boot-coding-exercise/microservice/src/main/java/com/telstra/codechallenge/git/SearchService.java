package com.telstra.codechallenge.git;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telstra.codechallenge.git.Response.ResponseBuilder;
import com.telstra.codechallenge.git.SearchController.SearchServiceException;

@Service
public class SearchService {

    @Value("${git.base.url}")
    private String gitBaseUrl;

    private RestTemplate restTemplate;

    public SearchService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Response getRepos(int records) throws IOException, SearchServiceException {
        
        Calendar calendar = Calendar.getInstance(); 
        String currentDate = getDate(calendar.getTime());
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        StringBuilder buildUrl = new StringBuilder(gitBaseUrl + "/repositories?q=created: < ");
        buildUrl.append(currentDate)
        .append("&q=created: > ").append(getDate(calendar.getTime()))
        .append("&sort=stars").append("&order=desc");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/vnd.github.mercy-preview+json");
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        Map<String, Object> map = bodyToObject(restTemplate.exchange(buildUrl.toString(), HttpMethod.GET, entity, String.class));
        return generateResponse(map, records, false);
    }
    
    public Response getAccounts(int records) throws IOException, SearchServiceException{
        
        StringBuilder buildUrl = new StringBuilder(gitBaseUrl + "/users?q=followers:0");
        buildUrl.append("&sort=joined").append("&order=asc");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/vnd.github.mercy-preview+json");
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        Map<String, Object> map = bodyToObject(restTemplate.exchange(buildUrl.toString(), HttpMethod.GET, entity, String.class));
        return generateResponse(map, records, true);
    }

    private Response generateResponse(Map<String, Object> map, int records, boolean isUserFetched) throws SearchServiceException{
        List items =  (List) map.get("items");

        if(items.size() < records){
            String isOrAre = items.size()>1 ? "are " : "is ";
            throw new SearchController.SearchServiceException("Fetched results " +  isOrAre + items.size()  + " which " + isOrAre + "lesser than the request results " + records);
        }
        ResponseBuilder response = Response.builder();
        return isUserFetched ? response.items(generateSearchUserResponse(items, records)).build()
                : response.items(generateSearchRepositoryResponse(items, records)).build();
    }
    
    private List<Item> generateSearchRepositoryResponse(List items, int records) throws SearchServiceException{
        List<Item> itemList = new ArrayList<>();
        for(int index = 0; index < records; index++){
            Repos repos = new Repos();
            repos.setHtmlUrl(((Map)items.get(index)).get("html_url").toString());
            repos.setWatchersCount(Integer.parseInt(((Map)items.get(index)).get("watchers_count").toString()));
            repos.setDescription(((Map)items.get(index)).get("description").toString());
            repos.setName(((Map)items.get(index)).get("name").toString());
            repos.setLanguage(((Map)items.get(index)).get("language").toString());
            itemList.add(repos);
        }
        return itemList;
        
    }
    
    private List<Item> generateSearchUserResponse(List items, int records) throws SearchServiceException {
        
        List<Item> itemList = new ArrayList<>();
        for(int index = 0; index < records; index++){
            Users user = new Users();
            user.setHtmlUrl(((Map)items.get(index)).get("html_url").toString());
            user.setId(Integer.parseInt(((Map)items.get(index)).get("id").toString()));
            user.setLogin(((Map)items.get(index)).get("login").toString());
            itemList.add(user);
        }
        return itemList;
    }
    

    private String getDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    public Map<String, Object> bodyToObject(ResponseEntity<String> responseEntity) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, Object> map = new HashMap<String, Object>();
            map = objectMapper.readValue(responseEntity.getBody(), new TypeReference<Map<String, Object>>(){});
            return map;
        } catch (IOException ex) {
            throw new IOException("Failed to map");
        }
    }
}
