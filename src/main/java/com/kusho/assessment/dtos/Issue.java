package com.kusho.assessment.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Issue {

    public long id;
    public int number;
    public String title;
    public String state;
    public String body;

    public IssueUser user;
    public List<IssueLabel> labels;

    public int comments;

    public LocalDateTime created_at;
    public LocalDateTime updated_at;
    public LocalDateTime closed_at;

    public Map<String, Object> pull_request;
}
