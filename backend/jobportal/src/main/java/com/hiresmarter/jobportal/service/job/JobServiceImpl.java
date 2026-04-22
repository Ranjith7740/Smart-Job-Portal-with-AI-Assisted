package com.hiresmarter.jobportal.service.job;


import com.hiresmarter.jobportal.entity.Job;
import com.hiresmarter.jobportal.exception.ResourceNotFoundException;
import com.hiresmarter.jobportal.repository.JobRepository;

import com.hiresmarter.jobportal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j // Industry standard for clean logging
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;

    // 1. Add UserRepository to your dependencies
    private final UserRepository userRepository;

    @Override
    @CacheEvict(value = {"jobs"}, allEntries = true)
    public Job createJob(Job job) {
        log.info("Creating new job posting: {}", job.getTitle());

        // 1. Get the Authentication object
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();

        // 2. Direct cast to your User entity (since you put the 'user' object in the filter)
        if (auth.getPrincipal() instanceof com.hiresmarter.jobportal.entity.User recruiter) {
            job.setRecruiter(recruiter);
            log.info("Linked job to recruiter: {}", recruiter.getEmail());
        } else {
            log.error("Principal is not an instance of User. Type found: {}", auth.getPrincipal().getClass().getName());
            throw new ResourceNotFoundException("Recruiter authentication failed");
        }

        Job savedJob = jobRepository.save(job);
        log.info("Job created successfully with ID: {}", savedJob.getId());
        return savedJob;
    }

    @Override
    @CacheEvict(value = {"jobs", "job"}, allEntries = true)
    public Job updateJob(Long jobId, Job updatedJob) {
        log.info("Updating Job ID: {}", jobId);
        Job job = getJobById(jobId);

        job.setTitle(updatedJob.getTitle());
        job.setDescription(updatedJob.getDescription());
        job.setRequiredSkills(updatedJob.getRequiredSkills());

        Job saved = jobRepository.save(job);
        log.info("Job ID: {} updated successfully. Related caches cleared.", jobId);
        return saved;
    }

    @Override
    @CacheEvict(value = {"jobs", "job"}, allEntries = true)
    public void deleteJob(Long jobId) {
        log.info("Request to delete Job ID: {}", jobId);
        Job job = getJobById(jobId);
        jobRepository.delete(job);
        log.info("Job ID: {} deleted. Caches cleared.", jobId);
    }

    @Override
    @Cacheable(value = "jobs")
    @Transactional(readOnly = true)
    public Page<Job> getAllJobs(Pageable pageable) {
        log.info("Fetching jobs page: {} with size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return jobRepository.findAll(pageable);
    }

    @Override
    @Cacheable(value = "job", key = "#id")
    @Transactional(readOnly = true)
    public Job getJobById(Long id) {
        // If this log appears, it means the result was NOT in the cache.
        log.info("Cache miss for Job ID: {}. Fetching from Database.", id);
        return jobRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Job lookup failed for ID: {}", id);
                    return new ResourceNotFoundException("Job not found");
                });
    }
}