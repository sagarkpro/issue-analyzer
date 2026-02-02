package com.cursed.github.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cursed.github.dtos.AnalyzeRepo;
import com.cursed.github.dtos.ScanRepoDto;
import com.cursed.github.services.IssueServices;

import jakarta.validation.Valid;

@RestController
@Validated
@RequestMapping("/api/issue")
public class IssueController {
    private final IssueServices issueServices;

    public IssueController(IssueServices issueServices) {
        this.issueServices = issueServices;
    }

    @PostMapping("/scan")
    public ResponseEntity<?> scanIssues(@RequestBody @Valid ScanRepoDto req) {
        try {
            return ResponseEntity.ok(issueServices.scanIssues(req));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeIssues(@RequestBody @Valid AnalyzeRepo req) {
        try {
            return ResponseEntity.ok(issueServices.analyzeIssues(req));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
