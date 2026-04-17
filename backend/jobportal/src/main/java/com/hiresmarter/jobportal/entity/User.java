package com.hiresmarter.jobportal.entity;


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
    private String password; // BCrypt hashed

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    /* Relationships */

    @OneToMany(mappedBy = "user")
    private List<Application> applications;

    @OneToMany(mappedBy = "recruiter")
    private List<Job> jobs;

    @OneToMany(mappedBy = "user")
    private List<Resume> resumes;
}