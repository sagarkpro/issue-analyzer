package com.kusho.assessment.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kusho.assessment.dtos.Issue;

import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GitHubIssuesClient {

    private static final String BASE_URL = "https://api.github.com";
    private static final int PER_PAGE = 25;

    private static final OkHttpClient client = new OkHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public static List<Issue> fetchAllIssues(String owner, String repo) throws IOException {

        List<Issue> allIssues = new ArrayList<>();
        int page = 1;

        while (true) {

            HttpUrl url = HttpUrl.parse(
                    BASE_URL + "/repos/" + owner + "/" + repo + "/issues").newBuilder()
                    .addQueryParameter("state", "open")
                    .addQueryParameter("per_page", String.valueOf(PER_PAGE))
                    .addQueryParameter("page", String.valueOf(page))
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .header("Accept", "application/vnd.github+json")
                    .build();

            try (Response response = client.newCall(request).execute()) {

                if (!response.isSuccessful()) {
                    throw new IOException("GitHub API error: " + response.code());
                }

                List<Issue> issues = mapper.readValue(
                        response.body().string(),
                        new TypeReference<List<Issue>>() {
                        });

                if (issues.isEmpty())
                    break;

                // filter out pull requests
                issues.stream()
                        .filter(i -> i.pull_request == null)
                        .forEach(allIssues::add);

                page++;
            }
        }

        return allIssues;
    }
}
