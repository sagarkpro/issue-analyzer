package com.kusho.assessment.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

        if (issues == null || issues.isEmpty()) {
            throw new IllegalArgumentException("No issues found for this repo. Scan it or open some issues, maybe?");
        }

        int chunkSize = 10;
        List<String> partialResults = new ArrayList<>();

        for (int i = 0; i < issues.size(); i += chunkSize) {
            try {
                List<?> chunk = issues.subList(i, Math.min(i + chunkSize, issues.size()));

                String chunkPrompt = req.getPrompt() +
                        "\n[PARTIAL DATA - ANALYZE THIS CHUNK ONLY]\n" +
                        mapper.writeValueAsString(chunk);

                GenerateContentResponse response = geminiClient.models.generateContent(
                        "gemini-3-flash-preview",
                        chunkPrompt,
                        null);

                partialResults.add(response.text());
            } catch (Exception e) {
                System.out.println("Error inside chunked for loop");
                System.out.println(e);
            }
        }

        if (partialResults.size() == 1) {
            return AnalysisResultDto.builder().analysis(partialResults.get(0)).build();
        }

        String finalPrompt = "I have analyzed a large number of issues in batches. " +
                "Here are the summaries of each batch. Please provide a final, cohesive analysis " +
                "based on the original request: " + req.getPrompt() + "\n\n" +
                String.join("\n---\n", partialResults);

        GenerateContentResponse finalResponse = geminiClient.models.generateContent(
                "gemini-3-flash-preview",
                finalPrompt,
                null);

        return AnalysisResultDto.builder().analysis(finalResponse.text()).build();
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
