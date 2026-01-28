package com.kusho.assessment.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IssueUser {
    public String login;
    public long id;
    public String avatar_url;
    public String html_url;
}
