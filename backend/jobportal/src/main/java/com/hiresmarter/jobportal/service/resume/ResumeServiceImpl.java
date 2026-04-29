package com.hiresmarter.jobportal.service.resume;

import com.hiresmarter.jobportal.dto.ai.AIResult;
import com.hiresmarter.jobportal.entity.Resume;
import com.hiresmarter.jobportal.entity.User;
import com.hiresmarter.jobportal.exception.BadRequestException;
import com.hiresmarter.jobportal.exception.ResourceNotFoundException;
import com.hiresmarter.jobportal.repository.ResumeRepository;
import com.hiresmarter.jobportal.repository.UserRepository;
import com.hiresmarter.jobportal.service.ai.AIService;
import com.hiresmarter.jobportal.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j // The industry standard for clean logging
public class ResumeServiceImpl implements ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final AIService aiService;

    @Override
    public Resume uploadResume(Long userId, MultipartFile file) {
        log.info("Resume upload initiated for User ID: {}. File: {}", userId, file.getOriginalFilename());

        if (!FileUtil.isValidPdf(file)) {
            log.warn("Invalid upload attempt: User {} tried to upload a non-PDF file.", userId);
            throw new BadRequestException("Only PDF files allowed");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Upload failed: User ID {} not found", userId);
                    return new ResourceNotFoundException("User not found");
                });

        // deactivate previous resumes
        log.debug("Deactivating old resumes for User ID: {}", userId);
        resumeRepository.findByUser(user).forEach(r -> r.setActive(false));

        // File operations - logging here is critical for debugging local paths
        String filePath = FileUtil.saveFile(file);
        log.info("File saved to local storage: {}", filePath);

        String extractedText = FileUtil.extractText(file);
        log.info("Text extraction complete. Character count: {}",
                (extractedText != null ? extractedText.length() : 0));

        Resume resume = Resume.builder()
                .user(user)
                .filePath(filePath)
                .extractedText(extractedText)
                .isActive(true)
                .build();

        Resume saved = resumeRepository.save(resume);
        log.info("New active resume saved with ID: {} for User: {}", saved.getId(), userId);
        return saved;
    }

    @Override
    public AIResult analyzeResumeForJob(Long resumeId, String jobDescription) {
        log.info("Analyzing Resume ID: {} for Job Description", resumeId);

        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));

        // 1. Just call the AI Service and return the result
        // Don't try to save anything to the 'resume' object here
        return aiService.analyzeResume(resume.getExtractedText(), jobDescription);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Resume> getUserResumes(Long userId) {
        log.debug("Fetching all resumes for User ID: {}", userId);
        return resumeRepository.findByUser(userRepository.findById(userId).orElseThrow());
    }

    @Override
    @Transactional(readOnly = true)
    public Resume getActiveResume(Long userId) {
        log.debug("Fetching current active resume for User ID: {}", userId);
        return resumeRepository.findByUserAndIsActiveTrue(userRepository.findById(userId).orElseThrow())
                .orElseGet(() -> {
                    log.info("No active resume found for User ID: {}", userId);
                    return null;
                });
    }
}