package com.hiresmarter.jobportal.service.auth;

import com.hiresmarter.jobportal.dto.auth.AuthRequest;
import com.hiresmarter.jobportal.dto.auth.AuthResponse;
import com.hiresmarter.jobportal.entity.User;
import com.hiresmarter.jobportal.enums.RoleType;
import com.hiresmarter.jobportal.enums.UserStatus;
import com.hiresmarter.jobportal.exception.BadRequestException;
import com.hiresmarter.jobportal.repository.UserRepository;
import com.hiresmarter.jobportal.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public AuthResponse register(AuthRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(RoleType.JOB_SEEKER)
                .status(UserStatus.ACTIVE)
                .build();

        userRepository.save(user);

        String token = jwtUtil.generateToken(user);

        return new AuthResponse(token, user.getRole());
    }

    @Override
    public AuthResponse login(AuthRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user);

        return new AuthResponse(token, user.getRole());
    }
}