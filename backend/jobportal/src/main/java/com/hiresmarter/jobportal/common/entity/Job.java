package com.hiresmarter.jobportal.common.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "jobs")
public class Job extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String title;
    private String description;
    private Integer minExperience;

    @ManyToOne
    private User recruiter;
}
