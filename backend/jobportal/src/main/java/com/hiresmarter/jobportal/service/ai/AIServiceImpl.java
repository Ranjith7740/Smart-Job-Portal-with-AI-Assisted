package com.hiresmarter.jobportal.service.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiresmarter.jobportal.dto.ai.AIResult;

import lombok.extern.slf4j.Slf4j; // The best way to log in Spring
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class AIServiceImpl implements AIService {

    @Value("${google.gemini.api.key}")
    private String apiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=";

    @Override
    public AIResult analyzeResume(String resumeText, String jobDescription) {
        RestTemplate restTemplate = new RestTemplate();

        // The Prompt: We force Gemini to return valid JSON so we can parse it easily
        String prompt = "Act as an expert HR Recruiter. Analyze the following resume against the job description.\n\n" +
                "Job Description: " + jobDescription + "\n\n" +
                "Resume Text: " + resumeText + "\n\n" +
                "Return ONLY a JSON object with these exact keys: " +
                "score (number 0-100), matchedSkills (string), missingSkills (string), feedback (string). " +
                "Do not include markdown formatting or backticks.";

        // Gemini Request Structure
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(Map.of(
                        "parts", List.of(Map.of("text", prompt))
                ))
        );

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(API_URL + apiKey, requestBody, String.class);
            return parseGeminiResponse(response.getBody());
        } catch (Exception e) {
            log.error("AI Analysis failed: {}", e.getMessage());
            return AIResult.builder().score(0.0).feedback("Analysis unavailable").build();
        }
    }

    private AIResult parseGeminiResponse(String responseBody) throws Exception {
        JsonNode root = objectMapper.readTree(responseBody);
        // Navigate the complex Gemini JSON structure: candidates.content.parts.text
        String rawJson = root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();

        // Clean any potential markdown backticks Gemini might add
        String cleanJson = rawJson.replaceAll("```json", "").replaceAll("```", "").trim();

        return objectMapper.readValue(cleanJson, AIResult.class);
    }
}