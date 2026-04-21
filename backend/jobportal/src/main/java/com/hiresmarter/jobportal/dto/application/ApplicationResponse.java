package com.hiresmarter.jobportal.dto.application;

import com.hiresmarter.jobportal.enums.ApplicationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ApplicationResponse {

    private Long applicationId;
    private String jobTitle;
    private ApplicationStatus status;
    private LocalDateTime appliedDate;
    private Double resumeScore;
}
