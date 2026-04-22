package com.hiresmarter.jobportal.service;

import com.hiresmarter.jobportal.entity.Job;
import com.hiresmarter.jobportal.entity.User;
import com.hiresmarter.jobportal.exception.ResourceNotFoundException;
import com.hiresmarter.jobportal.repository.JobRepository;
import com.hiresmarter.jobportal.service.job.JobServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock private JobRepository jobRepository;
    @Mock private Authentication authentication;
    @Mock private SecurityContext securityContext;

    @InjectMocks
    private JobServiceImpl jobService;

    private User mockRecruiter;
    private Job mockJob;

    @BeforeEach
    void setUp() {
        mockRecruiter = new User();
        mockRecruiter.setId(1L);
        mockRecruiter.setEmail("recruiter@cognizant.com");

        mockJob = new Job();
        mockJob.setId(50L);
        mockJob.setTitle("Software Engineer");
    }

    @Test
    @DisplayName("Should create job and link currently authenticated recruiter")
    void createJob_Success() {
        // We use MockedStatic to mock the static SecurityContextHolder
        try (MockedStatic<SecurityContextHolder> mockedContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(mockRecruiter);

            when(jobRepository.save(any(Job.class))).thenReturn(mockJob);

            Job result = jobService.createJob(new Job());

            assertThat(result).isNotNull();
            verify(jobRepository).save(argThat(job -> job.getRecruiter().equals(mockRecruiter)));
        }
    }

    @Test
    @DisplayName("Should throw exception when authentication principal is invalid")
    void createJob_AuthFailure_ThrowsException() {
        try (MockedStatic<SecurityContextHolder> mockedContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            // Simulating a case where principal is a String (like 'anonymousUser') instead of User entity
            when(authentication.getPrincipal()).thenReturn("anonymousUser");

            assertThrows(ResourceNotFoundException.class, () -> jobService.createJob(new Job()));
        }
    }

    @Test
    @DisplayName("Should fetch job from DB on cache miss")
    void getJobById_Success() {
        when(jobRepository.findById(50L)).thenReturn(Optional.of(mockJob));

        Job result = jobService.getJobById(50L);

        assertThat(result.getTitle()).isEqualTo("Software Engineer");
        verify(jobRepository, times(1)).findById(50L);
    }
}