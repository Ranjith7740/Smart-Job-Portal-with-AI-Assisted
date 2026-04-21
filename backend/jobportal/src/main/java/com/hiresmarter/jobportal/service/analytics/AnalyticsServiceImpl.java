package com.hiresmarter.jobportal.service.analytics;

import com.hiresmarter.jobportal.enums.ApplicationStatus;
import com.hiresmarter.jobportal.repository.ApplicationRepository;
import com.hiresmarter.jobportal.repository.JobRepository;
import com.hiresmarter.jobportal.repository.ResumeRepository;
import com.hiresmarter.jobportal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsServiceImpl implements AnalyticsService {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final ResumeRepository resumeRepository;

    @Override
    @Cacheable(value = "adminStats")
    public Map<String, Object> getAdminStats() {
        // 1. Track start time to monitor performance
        long startTime = System.currentTimeMillis();
        log.info("Fetching Admin Dashboard analytics...");

        Map<String, Object> stats = new HashMap<>();

        try {
            long totalApps = applicationRepository.count();
            long hiredApps = applicationRepository.countByStatus(ApplicationStatus.HIRED);

            stats.put("totalUsers", userRepository.count());
            stats.put("totalJobs", jobRepository.count());
            stats.put("totalApplications", totalApps);

            // Quality Metrics
            Double avgScore = resumeRepository.getGlobalAverageScore();
            if (avgScore == null) {
                log.warn("Average resume score calculation returned null (check if any scores exist)");
            }
            stats.put("averageResumeScore", avgScore != null ? Math.round(avgScore * 100.0) / 100.0 : 0.0);

            // Efficiency Metrics
            double conversionRate = totalApps > 0 ? ((double) hiredApps / totalApps) * 100 : 0;
            stats.put("hiringConversionRate", String.format("%.2f%%", conversionRate));

            // 2. Log final performance and summary
            long duration = System.currentTimeMillis() - startTime;
            log.info("Analytics calculated successfully in {}ms | Total Apps: {}", duration, totalApps);

        } catch (Exception e) {
            // 3. Essential error logging
            log.error("Failed to generate Admin Stats: {}", e.getMessage());
            throw e;
        }

        return stats;
    }
}