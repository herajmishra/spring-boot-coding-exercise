package com.telstra.codechallenge.git;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/search")
public class SearchController {

    @Autowired
    SearchService searchService;

    @RequestMapping(path = "/repositories/{records}", method = RequestMethod.GET)
    public Response searchRepoWithMaxStar(@PathVariable("records") int records) throws IOException, SearchServiceException {
        if(records<0){
            throw new SearchController.SearchServiceException("Requested number of repository cannot be negative integer");
        }
        return searchService.getRepos(records);
    }
    
    @RequestMapping(path = "/accounts/{records}", method = RequestMethod.GET)
    public Response searchOldestAccountsWithZeroFollowers(@PathVariable("records") int records) throws IOException, SearchServiceException {
        if(records<0){
            throw new SearchController.SearchServiceException("Requested number of accounts cannot be negative integer");
        }
        return searchService.getAccounts(records);
    }

    private static class ErrorResponse {
        public String message;

        public ErrorResponse(String message) {
            this.message = message;
        }
    }

    @ExceptionHandler(SearchServiceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(SearchServiceException e) {
        return new ErrorResponse(e.getMessage()); // use message from the original exception
    }

    static class SearchServiceException extends Exception {
        public SearchServiceException(String message) {
            super(message);
        }
    }

}

//https://api.github.com/search/repositories?q=created: < 2018-05-02&sort=stars&order=desc&q=created: > 2018-04-25