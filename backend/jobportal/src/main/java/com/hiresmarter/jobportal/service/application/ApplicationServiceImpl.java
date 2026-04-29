package com.hiresmarter.jobportal.service.application;

import com.hiresmarter.jobportal.dto.ai.AIResult;
import com.hiresmarter.jobportal.entity.Application;
import com.hiresmarter.jobportal.entity.Job;
import com.hiresmarter.jobportal.entity.Resume;
import com.hiresmarter.jobportal.entity.User;
import com.hiresmarter.jobportal.enums.ApplicationStatus;
import com.hiresmarter.jobportal.exception.BadRequestException;
import com.hiresmarter.jobportal.exception.ResourceNotFoundException;
import com.hiresmarter.jobportal.repository.ApplicationRepository;
import com.hiresmarter.jobportal.repository.JobRepository;
import com.hiresmarter.jobportal.repository.UserRepository;
import com.hiresmarter.jobportal.service.ai.AIService;
import com.hiresmarter.jobportal.service.notification.NotificationService;
import com.hiresmarter.jobportal.service.resume.ResumeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j // 1. Using Lombok to keep your service clean
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final ResumeService resumeService;
    private final AIService aiService;

    private static final Map<ApplicationStatus, List<ApplicationStatus>> ALLOWED_TRANSITIONS = Map.of(
            ApplicationStatus.APPLIED,   List.of(ApplicationStatus.SCREENING, ApplicationStatus.REJECTED),
            ApplicationStatus.SCREENING, List.of(ApplicationStatus.INTERVIEW, ApplicationStatus.REJECTED),
            ApplicationStatus.INTERVIEW, List.of(ApplicationStatus.OFFER, ApplicationStatus.REJECTED),
            ApplicationStatus.OFFER,     List.of(ApplicationStatus.HIRED, ApplicationStatus.REJECTED)
    );

    @Override
    public Application apply(Long jobId, Long userId) {
        log.info("New application request | UserID: {} -> JobID: {}", userId, jobId);

        if (applicationRepository.existsByJobIdAndUserId(jobId, userId)) {
            log.warn("Duplicate application blocked | UserID: {}, JobID: {}", userId, jobId);
            throw new BadRequestException("You have already applied for this position.");
        }

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with ID: " + jobId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        if (applicationRepository.findByUserAndJob(user, job).isPresent()) {
            log.warn("Duplicate application attempt | UserID: {}, JobID: {}", userId, jobId);
            throw new BadRequestException("You have already applied for this position.");
        }

        Application application = Application.builder()
                .job(job)
                .user(user)
                .status(ApplicationStatus.APPLIED)
                .appliedDate(LocalDateTime.now())
                .build();

        Application saved = applicationRepository.save(application);
        log.info("Application successfully submitted | AppID: {}", saved.getId());

        try {
            Resume activeResume = resumeService.getActiveResume(userId);
            if (activeResume != null) {
                // Get the result from the AI service
                AIResult aiResult = resumeService.analyzeResumeForJob(activeResume.getId(), job.getDescription());

                // Save the results to the APPLICATION record
                saved.setScore(aiResult.getScore());
                saved.setFeedback(aiResult.getFeedback());
                saved.setMatchedSkills(aiResult.getMatchedSkills());
                saved.setMissingSkills(aiResult.getMissingSkills());

                // Final save to update the application with AI data
                applicationRepository.save(saved);
            }
        } catch (Exception e) {
            log.error("AI Analysis failed but application was saved: {}", e.getMessage());
        }
        // ------------------------------------

        notificationService.notifyUser(userId, "Successfully applied for: " + job.getTitle());

        notificationService.notifyUser(userId, "Successfully applied for: " + job.getTitle());
        return saved;
    }
    @Override
    @Transactional(readOnly = true)
    public List<Application> getApplicationsForUser(Long userId) {
        log.info("Fetching all applications for User ID: {}", userId);

        // 1. Verify user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // 2. Fetch applications
        List<Application> applications = applicationRepository.findByUser(user);

        log.info("Found {} applications for User ID: {}", applications.size(), userId);
        return applications;
    }
    @Override
    @Transactional(readOnly = true)
    public List<Application> getApplicationsForJob(Long jobId) {
        log.info("Fetching all applications for Job ID: {}", jobId);

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with ID: " + jobId));

        List<Application> applications = applicationRepository.findByJob(job);

        // Ensure resumes are loaded for the initial list load
        applications.forEach(app -> {
            if (app.getUser() != null && app.getUser().getResumes() != null) {
                app.getUser().getResumes().size();
            }
        });

        log.info("Found {} applications for Job ID: {}", applications.size(), jobId);
        return applications;
    }

    @Override
    public Application updateStatus(Long applicationId, ApplicationStatus newStatus) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        ApplicationStatus currentStatus = application.getStatus();

        if (currentStatus == newStatus) {
            return application;
        }

        log.info("Attempting status update | AppID: {} | {} -> {}", applicationId, currentStatus, newStatus);

        boolean isValid = ALLOWED_TRANSITIONS
                .getOrDefault(currentStatus, List.of())
                .contains(newStatus);

        if (!isValid) {
            log.error("Pipeline violation | AppID: {} | {} to {} is forbidden", applicationId, currentStatus, newStatus);
            throw new BadRequestException(
                    String.format("Invalid pipeline transition: Cannot move from %s to %s", currentStatus, newStatus)
            );
        }

        application.setStatus(newStatus);
        Application updated = applicationRepository.save(application);

        log.info("Status updated successfully | AppID: {} | Current Status: {}", updated.getId(), newStatus);

        notificationService.notifyUser(
                application.getUser().getId(),
                "Your application status for " + application.getJob().getTitle() + " has been updated to: " + newStatus
        );

        return updated;
    }



    @Override
    @Transactional(readOnly = true)
    public List<Application> searchCandidates(Long jobId, ApplicationStatus status, Double minScore, String skill) {
        log.info("Candidate Search | JobID: {} | Status: {} | MinScore: {} | Skill: {}", jobId, status, minScore, skill);

        if (!jobRepository.existsById(jobId)) {
            throw new ResourceNotFoundException("Job not found with ID: " + jobId);
        }

        List<Application> results = applicationRepository.filterCandidates(jobId, status, minScore, skill);

        // CRITICAL: Force load resumes for each user
        results.forEach(app -> {
            if (app.getUser() != null && app.getUser().getResumes() != null) {
                // Simply calling .size() triggers the initialization of the proxy list
                app.getUser().getResumes().size();
            }
        });

        log.info("Search complete | JobID: {} | Found {} candidates with initialized resumes", jobId, results.size());
        return results;
    }
}