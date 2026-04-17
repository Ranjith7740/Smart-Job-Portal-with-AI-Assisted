package com.hiresmarter.jobportal.common.entity;

import com.hiresmarter.jobportal.common.enums.RoleType;
import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private RoleType roleType;
}