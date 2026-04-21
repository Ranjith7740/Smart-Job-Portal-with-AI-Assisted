package com.hiresmarter.jobportal.dto.analytics;



import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AnalyticsResponse {

    private long totalUsers;
    private long totalJobs;
    private long totalApplications;
    private double averageResumeScore;
}