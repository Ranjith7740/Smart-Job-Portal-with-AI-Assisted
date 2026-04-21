package com.hiresmarter.jobportal.controller;

import com.hiresmarter.jobportal.entity.Application;
import com.hiresmarter.jobportal.enums.ApplicationStatus;
import com.hiresmarter.jobportal.service.application.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping("/apply")
    public ResponseEntity<Application> applyToJob(
            @RequestParam Long jobId,
            @RequestParam Long userId) {

        return ResponseEntity.ok(applicationService.apply(jobId, userId));
    }

    @PutMapping("/status")
    public ResponseEntity<Application> updateStatus(
            @RequestParam Long applicationId,
            @RequestParam ApplicationStatus status) {

        return ResponseEntity.ok(
                applicationService.updateStatus(applicationId, status));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Application>> getUserApplications(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                applicationService.getApplicationsForUser(userId));
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<Application>> getJobApplications(
            @PathVariable Long jobId) {

        return ResponseEntity.ok(
                applicationService.getApplicationsForJob(jobId));
    }
}