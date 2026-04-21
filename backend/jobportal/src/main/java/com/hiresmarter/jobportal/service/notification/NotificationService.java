package com.hiresmarter.jobportal.service.notification;

public interface NotificationService {

    void notifyUser(Long userId, String message);
    void sendEmail(String to, String subject, String body);
}