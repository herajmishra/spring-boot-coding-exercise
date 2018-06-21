package com.telstra.codechallenge.git;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Repos implements Item{
    
    @JsonProperty("html_url")
    private String htmlUrl;
    @JsonProperty("watchers_count")
    private int watchersCount;
    private String language;
    private String description;
    private String name;
}
