package com.hiresmarter.jobportal.service.job;

import com.hiresmarter.jobportal.entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JobService {

    Job createJob(Job job);

    Job updateJob(Long jobId, Job job);

    void deleteJob(Long jobId);

    Page<Job> getAllJobs(Pageable pageable);

    Job getJobById(Long id);
}