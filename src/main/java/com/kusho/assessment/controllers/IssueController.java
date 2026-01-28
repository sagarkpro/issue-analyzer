package com.kusho.assessment.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kusho.assessment.dtos.AnalyzeRepo;
import com.kusho.assessment.dtos.GitHubIssues;
import com.kusho.assessment.dtos.ScanRepoDto;
import com.kusho.assessment.services.IssueServices;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestParam;

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
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeIssues(@RequestBody @Valid AnalyzeRepo req) {
        try {
            return ResponseEntity.ok(issueServices.analyzeIssues(req));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
