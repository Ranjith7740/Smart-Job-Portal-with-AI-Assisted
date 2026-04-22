package com.hiresmarter.jobportal.service;

import com.hiresmarter.jobportal.entity.Notification;
import com.hiresmarter.jobportal.entity.User;
import com.hiresmarter.jobportal.repository.NotificationRepository;
import com.hiresmarter.jobportal.repository.UserRepository;
import com.hiresmarter.jobportal.service.notification.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock private NotificationRepository notificationRepository;
    @Mock private UserRepository userRepository;
    @Mock private JavaMailSender mailSender;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private User mockUser;
    private String testMessage = "Your application is under review.";

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("ranjith@test.com");
    }

    @Test
    @DisplayName("Should save notification and trigger email call")
    void notifyUser_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        // Act
        notificationService.notifyUser(1L, testMessage);

        // Assert
        // 1. Verify DB Save
        verify(notificationRepository, times(1)).save(any(Notification.class));

        // 2. Verify Email Sender was triggered
        // Note: In unit tests, @Async is bypassed by Mockito unless configured otherwise,
        // so we can verify the call directly.
        ArgumentCaptor<SimpleMailMessage> mailCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(mailCaptor.capture());

        SimpleMailMessage sentMail = mailCaptor.getValue();
        assertThat(sentMail.getTo()).contains("ranjith@test.com");
        assertThat(sentMail.getSubject()).isEqualTo("HireSmarter Update");
        assertThat(sentMail.getText()).isEqualTo(testMessage);
    }

    @Test
    @DisplayName("Should log warning and skip email if user not found")
    void notifyUser_UserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        notificationService.notifyUser(99L, testMessage);

        // Should still save the in-app notification attempt based on your logic
        verify(notificationRepository).save(any(Notification.class));
        // Should NOT trigger email
        verifyNoInteractions(mailSender);
    }
}