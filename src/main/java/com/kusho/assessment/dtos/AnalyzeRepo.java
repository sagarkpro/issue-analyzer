package com.kusho.assessment.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnalyzeRepo {
    @NotNull
    String repo;
    @NotNull
    String prompt;
}
