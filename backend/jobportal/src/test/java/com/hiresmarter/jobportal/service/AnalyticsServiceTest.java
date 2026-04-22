package com.hiresmarter.jobportal.service;

import com.hiresmarter.jobportal.enums.ApplicationStatus;
import com.hiresmarter.jobportal.repository.ApplicationRepository;
import com.hiresmarter.jobportal.repository.JobRepository;
import com.hiresmarter.jobportal.repository.ResumeRepository;
import com.hiresmarter.jobportal.repository.UserRepository;
import com.hiresmarter.jobportal.service.analytics.AnalyticsServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private JobRepository jobRepository;
    @Mock
    private ApplicationRepository applicationRepository;
    @Mock
    private ResumeRepository resumeRepository;

    @InjectMocks
    private AnalyticsServiceImpl analyticsService;

    @Test
    @DisplayName("Should calculate all stats correctly including conversion rate")
    void getAdminStats_Success() {
        // 1. Mock Repository Responses
        when(userRepository.count()).thenReturn(100L);
        when(jobRepository.count()).thenReturn(20L);
        when(applicationRepository.count()).thenReturn(50L);
        when(applicationRepository.countByStatus(ApplicationStatus.HIRED)).thenReturn(5L);
        when(resumeRepository.getGlobalAverageScore()).thenReturn(85.456);

        // 2. Act
        Map<String, Object> stats = analyticsService.getAdminStats();

        // 3. Assert
        assertThat(stats).isNotNull();
        assertThat(stats.get("totalUsers")).isEqualTo(100L);
        assertThat(stats.get("totalJobs")).isEqualTo(20L);
        assertThat(stats.get("totalApplications")).isEqualTo(50L);

        // Check rounding logic (85.456 -> 85.46)
        assertThat(stats.get("averageResumeScore")).isEqualTo(85.46);

        // Check conversion rate formatting (5/50 = 10%)
        assertThat(stats.get("hiringConversionRate")).isEqualTo("10.00%");
    }

    @Test
    @DisplayName("Should handle null average score and zero applications gracefully")
    void getAdminStats_EmptyData() {
        // Mock empty scenario
        when(applicationRepository.count()).thenReturn(0L);
        when(resumeRepository.getGlobalAverageScore()).thenReturn(null);

        Map<String, Object> stats = analyticsService.getAdminStats();

        assertThat(stats.get("averageResumeScore")).isEqualTo(0.0);
        assertThat(stats.get("hiringConversionRate")).isEqualTo("0.00%");
    }
}