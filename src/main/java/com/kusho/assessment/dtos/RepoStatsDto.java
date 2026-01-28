package com.kusho.assessment.dtos;

import lombok.Builder;
import lombok.Data;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
@Data
public class RepoStatsDto {

    String repo;

    int issuesFetched;

    boolean cachedSuccessfully;
}