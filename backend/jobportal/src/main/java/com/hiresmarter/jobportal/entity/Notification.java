package com.hiresmarter.jobportal.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification extends BaseEntity {

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 1000)
    private String message;

    private boolean isRead;
}
