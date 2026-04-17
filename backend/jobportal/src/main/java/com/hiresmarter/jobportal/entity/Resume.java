package com.hiresmarter.jobportal.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "resumes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resume extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String filePath;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String extractedText;

    private Double score;

    @Column(length = 3000)
    private String feedback;

    private boolean isActive;
}
