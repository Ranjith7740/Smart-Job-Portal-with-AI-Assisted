package com.hiresmarter.jobportal.service;


import com.hiresmarter.jobportal.entity.Application;
import com.hiresmarter.jobportal.entity.Job;
import com.hiresmarter.jobportal.entity.User;
import com.hiresmarter.jobportal.enums.ApplicationStatus;
import com.hiresmarter.jobportal.exception.BadRequestException;
import com.hiresmarter.jobportal.exception.ResourceNotFoundException;
import com.hiresmarter.jobportal.repository.ApplicationRepository;
import com.hiresmarter.jobportal.repository.JobRepository;
import com.hiresmarter.jobportal.repository.UserRepository;
import com.hiresmarter.jobportal.service.application.ApplicationServiceImpl;
import com.hiresmarter.jobportal.service.notification.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;
    @Mock
    private JobRepository jobRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ApplicationServiceImpl applicationService;

    private User testUser;
    private Job testJob;
    private Application testApplication;

    @BeforeEach
    void setUp() {
        // Instead of User.builder().id(1L)...
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("ranjith@example.com");

        testJob = new Job();
        testJob.setId(101L);
        testJob.setTitle("Java Developer");

        testApplication = Application.builder()
                .user(testUser)
                .job(testJob)
                .status(ApplicationStatus.APPLIED)
                .build();

// Manually set the ID after building
        testApplication.setId(501L);
    }

    @Test
    @DisplayName("Should successfully apply to a job")
    void apply_Success() {
        // Arrange
        when(jobRepository.findById(101L)).thenReturn(Optional.of(testJob));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(applicationRepository.findByUserAndJob(testUser, testJob)).thenReturn(Optional.empty());
        when(applicationRepository.save(any(Application.class))).thenReturn(testApplication);

        // Act
        Application result = applicationService.apply(101L, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(ApplicationStatus.APPLIED, result.getStatus());
        verify(notificationService, times(1)).notifyUser(eq(1L), anyString());
        verify(applicationRepository, times(1)).save(any(Application.class));
    }

    @Test
    @DisplayName("Should throw BadRequestException for duplicate application")
    void apply_Duplicate_ThrowsException() {
        // Arrange
        when(jobRepository.findById(101L)).thenReturn(Optional.of(testJob));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(applicationRepository.findByUserAndJob(testUser, testJob)).thenReturn(Optional.of(testApplication));

        // Act & Assert
        assertThrows(BadRequestException.class, () -> applicationService.apply(101L, 1L));
        verify(applicationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update status for valid transition (APPLIED -> SCREENING)")
    void updateStatus_ValidTransition() {
        // Arrange
        when(applicationRepository.findById(501L)).thenReturn(Optional.of(testApplication));
        when(applicationRepository.save(any(Application.class))).thenReturn(testApplication);

        // Act
        Application updated = applicationService.updateStatus(501L, ApplicationStatus.SCREENING);

        // Assert
        assertEquals(ApplicationStatus.SCREENING, updated.getStatus());
        verify(notificationService).notifyUser(anyLong(), contains("SCREENING"));
    }

    @Test
    @DisplayName("Should throw BadRequestException for invalid transition (APPLIED -> HIRED)")
    void updateStatus_InvalidTransition_ThrowsException() {
        // Arrange
        when(applicationRepository.findById(501L)).thenReturn(Optional.of(testApplication));

        // Act & Assert
        assertThrows(BadRequestException.class, () ->
                applicationService.updateStatus(501L, ApplicationStatus.HIRED)
        );
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when Job ID is missing")
    void getApplicationsForJob_JobNotFound() {
        // Arrange
        when(jobRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> applicationService.getApplicationsForJob(999L));
    }
}