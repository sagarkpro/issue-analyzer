package com.kusho.assessment.dtos;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class GitHubIssues {
    private final Map<String, List<Issue>> issuesMap = new ConcurrentHashMap<>();
    private static final GitHubIssues INSTANCE = new GitHubIssues();

    // Private constructor so nobody can 'new' their way out of this
    private GitHubIssues() {
    }

    public static GitHubIssues getInstance() {
        return INSTANCE;
    }
}