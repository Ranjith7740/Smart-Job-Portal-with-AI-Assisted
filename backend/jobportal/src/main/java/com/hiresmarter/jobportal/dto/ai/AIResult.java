package com.hiresmarter.jobportal.dto.ai;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AIResult {

    private double score;
    private String matchedSkills;
    private String missingSkills;
    private String feedback;
}
