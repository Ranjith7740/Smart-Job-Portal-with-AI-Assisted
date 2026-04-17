package com.hiresmarter.jobportal.repository;

public interface AnalyticsRepository {

    long countTotalUsers();
    long countTotalJobs();
    double getAverageResumeScore();
}
