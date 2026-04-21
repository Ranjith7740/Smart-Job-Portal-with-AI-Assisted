package com.hiresmarter.jobportal.dto.job;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JobResponse {

    private Long id;
    private String title;
    private String description;
    private String requiredSkills;
    private String recruiterName;
}
