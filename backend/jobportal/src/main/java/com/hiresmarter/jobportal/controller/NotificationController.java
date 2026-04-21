package com.hiresmarter.jobportal.controller;

import com.hiresmarter.jobportal.entity.Notification;
import com.hiresmarter.jobportal.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepository;

    @GetMapping("/{userId}")
    public ResponseEntity<List<Notification>> getUserNotifications(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                notificationRepository
                        .findByUserIdOrderByCreatedAtDesc(userId));
    }
}