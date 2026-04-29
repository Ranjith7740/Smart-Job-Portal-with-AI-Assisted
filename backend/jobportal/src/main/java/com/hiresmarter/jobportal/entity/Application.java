package com.hiresmarter.jobportal.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hiresmarter.jobportal.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "applications",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "job_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Application extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    // REMOVED "resumes" FROM THE LIST BELOW
    @JsonIgnoreProperties({"applications", "jobs", "hibernateLazyInitializer", "handler"})
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    @JsonIgnoreProperties({"applications", "recruiter", "hibernateLazyInitializer", "handler"})
    private Job job;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    private LocalDateTime appliedDate;

    private Double score;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @Column(length = 1000)
    private String matchedSkills;

    @Column(length = 1000)
    private String missingSkills;
}