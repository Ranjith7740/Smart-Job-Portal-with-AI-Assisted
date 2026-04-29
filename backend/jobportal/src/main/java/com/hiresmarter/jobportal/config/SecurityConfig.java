package com.hiresmarter.jobportal.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(org.springframework.security.config.Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // 1. Public & Documentation
                        .requestMatchers("/api/auth/**", "/swagger-ui/**", "/api-docs/**").permitAll()

                        // 2. Job Access (The Fix)
                        // Let Job Seekers AND Recruiters view the list/details
                        .requestMatchers(HttpMethod.GET, "/api/jobs/**").hasAnyRole("JOB_SEEKER", "RECRUITER")
                        // Alternatively: .requestMatchers(HttpMethod.GET, "/api/jobs/**").permitAll()

                        // Only Recruiters can create, update, or delete jobs
                        .requestMatchers(HttpMethod.POST, "/api/jobs/**").hasRole("RECRUITER")
                        .requestMatchers(HttpMethod.PUT, "/api/jobs/**").hasRole("RECRUITER")
                        .requestMatchers(HttpMethod.DELETE, "/api/jobs/**").hasRole("RECRUITER")

                        // 3. Applications
                        .requestMatchers("/api/applications/job/**").hasRole("RECRUITER")
                        .requestMatchers("/api/applications/apply/**").hasRole("JOB_SEEKER")
                        .requestMatchers("/api/applications/user/**").hasRole("JOB_SEEKER")

                        // 4. Fallbacks
                        .requestMatchers("/api/resumes/**").hasRole("JOB_SEEKER")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );


        return http.build();
    }
}