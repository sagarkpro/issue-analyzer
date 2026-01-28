package com.kusho.assessment.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api")
public class HealthController {
    @GetMapping("/health")
    public ResponseEntity<?> isAlive() {
        return ResponseEntity.ok("I am alive.");
    }
}
