package com.hiresmarter.jobportal.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 5000)
    private String description;

    @Column(nullable = false)
    private String requiredSkills; // comma-separated or later normalized

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruiter_id", nullable = false)
    @JsonIgnoreProperties({
            "resumes",
            "jobs",
            "applications",
            "hibernateLazyInitializer",
            "handler",
            "password" // Safety first!
    })
    private User recruiter;

    @OneToMany(mappedBy = "job")
    @JsonIgnore
    private List<Application> applications;
}