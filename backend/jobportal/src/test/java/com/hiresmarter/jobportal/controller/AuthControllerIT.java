package com.hiresmarter.jobportal.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiresmarter.jobportal.dto.auth.AuthRequest;
import com.hiresmarter.jobportal.dto.auth.AuthResponse;
import com.hiresmarter.jobportal.service.auth.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import com.hiresmarter.jobportal.enums.RoleType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // Disables Spring Security filters for this specific test
class AuthControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private AuthRequest authRequest;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        // FIX 1: Use the Builder to avoid "actual and formal argument lists differ" errors
        // Even if you add more fields later, the builder won't break your code.
        authRequest = AuthRequest.builder()
                .email("ranjith@example.com")
                .password("password123")
                .name("R. Ranjith") // Added name since it's in your DTO
                .build();

        // FIX 2: Match the RoleType Enum values (ADMIN, RECRUITER, or JOB_SEEKER)
        // Ensure AuthResponse constructor/builder takes RoleType and NOT String
        authResponse = AuthResponse.builder()
                .token("mocked-jwt-token")
                .role(RoleType.JOB_SEEKER) // Use the Enum directly
                .build();
    }

    @Test
    @DisplayName("POST /api/auth/register - Success")
    void register_Success() throws Exception {
        // Arrange
        when(authService.register(any(AuthRequest.class))).thenReturn(authResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked-jwt-token"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("POST /api/auth/login - Success")
    void login_Success() throws Exception {
        // Arrange
        when(authService.login(any(AuthRequest.class))).thenReturn(authResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }
}