package com.hiresmarter.jobportal.service.ai;


import com.hiresmarter.jobportal.dto.ai.AIResult;

public interface AIService {

    AIResult analyzeResume(String resumeText, String jobDescription);
}