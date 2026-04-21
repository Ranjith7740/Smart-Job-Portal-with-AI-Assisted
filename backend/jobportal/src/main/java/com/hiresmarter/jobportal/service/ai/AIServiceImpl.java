package com.hiresmarter.jobportal.service.ai;

import com.hiresmarter.jobportal.dto.ai.AIResult;
import lombok.extern.slf4j.Slf4j; // The best way to log in Spring
import org.springframework.stereotype.Service;

@Service
@Slf4j // Automatically creates a 'log' variable
public class AIServiceImpl implements AIService {

    @Override
    public AIResult analyzeResume(String resumeText, String jobDescription) {
        // 1. Log Input (Check if data actually reached the service)
        log.info("Starting AI Analysis. Resume length: {} chars, JD length: {} chars",
                resumeText.length(), jobDescription.length());

        try {
            // Mocked AI response (In production, log the raw response from the LLM here)
            AIResult result = AIResult.builder()
                    .score(78.5)
                    .matchedSkills("Java, Spring Boot")
                    .missingSkills("Redis, Docker")
                    .feedback("Strong backend skills, improve devops stack")
                    .build();

            // 2. Log Outcome (Success)
            log.info("AI Analysis completed. Assigned Score: {}", result.getScore());

            return result;

        } catch (Exception e) {
            // 3. Log Failure (Very important for office laptop debugging)
            log.error("Error during AI Resume Analysis: {}", e.getMessage());
            throw e;
        }
    }
}