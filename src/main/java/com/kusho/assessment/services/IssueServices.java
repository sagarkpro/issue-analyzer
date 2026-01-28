package com.kusho.assessment.services;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.kusho.assessment.dtos.AnalysisResultDto;
import com.kusho.assessment.dtos.AnalyzeRepo;
import com.kusho.assessment.dtos.GitHubIssues;
import com.kusho.assessment.dtos.Issue;
import com.kusho.assessment.dtos.RepoStatsDto;
import com.kusho.assessment.dtos.ScanRepoDto;

@Service
public class IssueServices {
    record RepoCoords(String owner, String name) {
    }

    private static Client geminiClient;
    private static ObjectMapper mapper;

    public IssueServices(@Value("${google.gemini.api-key}") String geminiApiKey, ObjectMapper mapper) {
        this.geminiClient = new Client.Builder().apiKey(geminiApiKey).build();
        this.mapper = mapper;
    }

    public AnalysisResultDto analyzeIssues(AnalyzeRepo req) throws JsonProcessingException {
        var issueMap = GitHubIssues.getInstance().getIssuesMap();
        var issues = issueMap.get(req.getRepo());

        StringBuilder prompt = new StringBuilder(req.getPrompt());
        prompt.append("\n These are the current open issues in git:\n");
        prompt.append(mapper.writeValueAsString(issues));
        GenerateContentResponse response = geminiClient.models.generateContent(
                "gemini-3-flash-preview",
                prompt.toString(),
                null);

        return AnalysisResultDto.builder().analysis(response.text()).build();
    }

    public RepoStatsDto scanIssues(ScanRepoDto req) throws IOException {
        var repository = validateRepoDto(req);

        var issueMap = GitHubIssues.getInstance().getIssuesMap();
        var issues = fetchAllIssues(repository.owner, repository.name);
        issueMap.putIfAbsent(req.getRepo(), issues);

        return RepoStatsDto
                .builder()
                .cachedSuccessfully(true)
                .issuesFetched(issues.size())
                .repo(req.getRepo())
                .build();
    }

    // *********** PRIVATE METHODS **************
    private List<Issue> fetchAllIssues(String owner, String repo) throws IOException {
        return Collections.unmodifiableList(
                GitHubIssuesClient.fetchAllIssues(owner, repo));
    }

    private RepoCoords validateRepoDto(ScanRepoDto req) {
        if (!req.getRepo().contains("/")) {
            throw new IllegalArgumentException("repo must be a valid repo input, e.g., sagarkpro/webhook-tester");
        }
        String[] repo = req.getRepo().split("/");
        if (repo.length != 2) {
            throw new IllegalArgumentException("repo must be a valid repo input, e.g., sagarkpro/webhook-tester");
        }
        return new RepoCoords(repo[0], repo[1]);
    }
}
