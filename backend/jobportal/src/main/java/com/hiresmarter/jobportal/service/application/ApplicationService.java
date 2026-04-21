package com.hiresmarter.jobportal.service.application;


import com.hiresmarter.jobportal.entity.Application;
import com.hiresmarter.jobportal.enums.ApplicationStatus;

import java.util.List;

public interface ApplicationService {

    Application apply(Long jobId, Long userId);

    Application updateStatus(Long applicationId, ApplicationStatus status);

    List<Application> getApplicationsForUser(Long userId);

    List<Application> getApplicationsForJob(Long jobId);

    List<Application> searchCandidates(Long jobId, ApplicationStatus status, Double minScore, String skill);
}