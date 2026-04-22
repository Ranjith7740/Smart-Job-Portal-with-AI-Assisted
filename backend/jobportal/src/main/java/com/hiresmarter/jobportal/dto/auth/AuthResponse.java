package com.hiresmarter.jobportal.dto.auth;

import com.hiresmarter.jobportal.entity.Role;
import com.hiresmarter.jobportal.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String token;
    private RoleType role;
}
