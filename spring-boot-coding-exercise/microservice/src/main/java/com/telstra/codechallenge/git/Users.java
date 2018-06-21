package com.telstra.codechallenge.git;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Users implements Item{
    
    private int id;
    private String login;
    @JsonProperty("html_url")
    private String htmlUrl;
}
