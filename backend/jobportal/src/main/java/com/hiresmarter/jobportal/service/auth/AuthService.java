package com.hiresmarter.jobportal.service.auth;


import com.hiresmarter.jobportal.dto.auth.AuthRequest;
import com.hiresmarter.jobportal.dto.auth.AuthResponse;

public interface AuthService {

    AuthResponse register(AuthRequest request);

    AuthResponse login(AuthRequest request);
}