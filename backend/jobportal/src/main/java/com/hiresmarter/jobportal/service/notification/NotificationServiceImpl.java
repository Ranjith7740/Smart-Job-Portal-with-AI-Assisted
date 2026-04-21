package com.hiresmarter.jobportal.service.notification;

import com.hiresmarter.jobportal.entity.Notification;
import com.hiresmarter.jobportal.repository.NotificationRepository;
import com.hiresmarter.jobportal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j // Professional logging
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    @Override
    @Transactional
    public void notifyUser(Long userId, String message) {
        log.info("Creating notification for User ID: {}", userId);

        // 1. Save In-App Notification
        Notification notification = Notification.builder()
                .userId(userId)
                .message(message)
                .isRead(false)
                .build();
        notificationRepository.save(notification);
        log.info("In-app notification saved successfully for User ID: {}", userId);

        // 2. Trigger Email
        userRepository.findById(userId).ifPresentOrElse(user -> {
            log.info("Handing off email delivery to background thread for: {}", user.getEmail());
            sendEmail(user.getEmail(), "HireSmarter Update", message);
        }, () -> log.warn("Notification skipped: User ID {} not found", userId));
    }

    @Async
    public void sendEmail(String to, String subject, String body) {
        // Log start of background task
        log.debug("Async thread started: Sending email to {}", to);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(body);
        mailMessage.setFrom("noreply@hiresmarter.com");

        try {
            mailSender.send(mailMessage);
            log.info("Email delivery successful to: {}", to);
        } catch (Exception e) {
            // Very important for Office Firewall/Mailtrap troubleshooting
            log.error("CRITICAL: Email delivery failed to {}. Error: {}", to, e.getMessage());
        }
    }
}