package com.hiresmarter.jobportal.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hiresmarter.jobportal.enums.RoleType;
import com.hiresmarter.jobportal.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Entity
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_user_email", columnList = "email")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @JsonIgnore
    private String password; // BCrypt hashed

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleType role;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    /* Relationships */

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Application> applications;

    @OneToMany(mappedBy = "recruiter")
    @JsonIgnore
    private List<Job> jobs;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Resume> resumes;
}