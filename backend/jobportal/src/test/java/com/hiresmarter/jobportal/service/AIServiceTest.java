package com.hiresmarter.jobportal.service;

import com.hiresmarter.jobportal.dto.ai.AIResult;
import com.hiresmarter.jobportal.service.ai.AIServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class AIServiceTest {

    @InjectMocks
    private AIServiceImpl aiService;

    private String mockResume;
    private String mockJd;

    @BeforeEach
    void setUp() {
        mockResume = "I am a Java Full Stack developer with experience in Spring Boot.";
        mockJd = "Looking for a Java developer proficient in Spring Boot and Microservices.";
    }

    @Test
    @DisplayName("Should return mock AI result when inputs are valid")
    void analyzeResume_Success() {
        // Act
        AIResult result = aiService.analyzeResume(mockResume, mockJd);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getScore()).isEqualTo(78.5);
        assertThat(result.getMatchedSkills()).contains("Java");
        assertThat(result.getMissingSkills()).contains("Redis");
        assertThat(result.getFeedback()).isEqualTo("Strong backend skills, improve devops stack");
    }

    @Test
    @DisplayName("Should throw NullPointerException when resumeText is null")
    void analyzeResume_NullResume_ThrowsException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            aiService.analyzeResume(null, mockJd);
        });
    }

    @Test
    @DisplayName("Should handle empty strings correctly")
    void analyzeResume_EmptyInputs_Success() {
        // Even with empty strings, the current mock implementation returns 78.5
        AIResult result = aiService.analyzeResume("", "");

        assertThat(result).isNotNull();
        assertThat(result.getScore()).isEqualTo(78.5);
    }
}