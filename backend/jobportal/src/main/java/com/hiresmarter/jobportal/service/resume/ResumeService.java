package com.hiresmarter.jobportal.service.resume;

import com.hiresmarter.jobportal.entity.Resume;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ResumeService {

    Resume uploadResume(Long userId, MultipartFile file);

    List<Resume> getUserResumes(Long userId);

    Resume getActiveResume(Long userId);

    Resume analyzeResumeForJob(Long resumeId, String jobDescription);
}