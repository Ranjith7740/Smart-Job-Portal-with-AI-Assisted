package com.hiresmarter.jobportal.dto.resume;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResumeResponse {

    private Long id;
    private String filePath;
    private Double score;
    private String feedback;
    private boolean active;
}
