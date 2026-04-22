package com.hiresmarter.jobportal.controller;

import com.hiresmarter.jobportal.dto.ai.AIResult;
import com.hiresmarter.jobportal.entity.Resume;
import com.hiresmarter.jobportal.service.resume.ResumeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
@Slf4j
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping("/upload")
    public ResponseEntity<Resume> uploadResume(
            @RequestParam Long userId,
            @RequestParam MultipartFile file) {

        return ResponseEntity.ok(resumeService.uploadResume(userId, file));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Resume>> getUserResumes(
            @PathVariable Long userId) {

        return ResponseEntity.ok(resumeService.getUserResumes(userId));
    }

    @GetMapping("/active/{userId}")
    public ResponseEntity<Resume> getActiveResume(
            @PathVariable Long userId) {

        return ResponseEntity.ok(resumeService.getActiveResume(userId));
    }
    @PostMapping("/{resumeId}/analyze")
    public ResponseEntity<Resume> analyzeResume(
            @PathVariable Long resumeId,
            @RequestBody String jobDescription) {
        
        return ResponseEntity.ok(resumeService.analyzeResumeForJob(resumeId, jobDescription));
    }

}