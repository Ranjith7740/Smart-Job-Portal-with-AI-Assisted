package com.hiresmarter.jobportal.dto.user;

import com.hiresmarter.jobportal.entity.Role;
import com.hiresmarter.jobportal.enums.UserStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private Role role;
    private UserStatus status;
}