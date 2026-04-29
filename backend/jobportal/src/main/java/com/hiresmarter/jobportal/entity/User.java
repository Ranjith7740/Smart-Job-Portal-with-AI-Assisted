package com.hiresmarter.jobportal.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Resume> resumes;
}