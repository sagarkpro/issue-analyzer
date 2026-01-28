package com.kusho.assessment.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IssueLabel {
    public long id;
    public String name;
    public String color;
    public boolean is_default;
}
