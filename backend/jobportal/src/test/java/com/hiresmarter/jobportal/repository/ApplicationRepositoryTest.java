package com.hiresmarter.jobportal.repository;




import com.hiresmarter.jobportal.entity.Application;
import com.hiresmarter.jobportal.entity.Job;
import com.hiresmarter.jobportal.entity.Resume;
import com.hiresmarter.jobportal.entity.User;
import com.hiresmarter.jobportal.enums.ApplicationStatus;
import com.hiresmarter.jobportal.enums.RoleType;
import com.hiresmarter.jobportal.enums.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ApplicationRepositoryTest {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Job savedJob;
    private User candidate;

    @BeforeEach
    void setUp() {
        // 1. Create and Persist a Recruiter
        User recruiter = new User();
        recruiter.setEmail("hr.manager@hiresmarter.com");
        recruiter.setName("HR Manager");
        recruiter.setPassword("encodedPassword");
        recruiter.setRole(RoleType.RECRUITER);
        recruiter.setStatus(UserStatus.ACTIVE); // Added status just in case it's NOT NULL
        recruiter = entityManager.persist(recruiter);

        // 2. Create and Persist a Job
        savedJob = new Job();
        savedJob.setTitle("Java Full Stack Developer");
        savedJob.setDescription("Develop high-quality software solutions.");
        savedJob.setRequiredSkills("Java, Spring Boot, MySQL");
        savedJob.setRecruiter(recruiter);
        savedJob = entityManager.persist(savedJob);

        // 3. Create and Persist a Candidate User
        candidate = new User();
        candidate.setEmail("ranjith.candidate@test.com");
        candidate.setName("R. Ranjith");
        candidate.setPassword("password123");
        candidate.setRole(RoleType.JOB_SEEKER);
        candidate.setStatus(UserStatus.ACTIVE); // Added status
        candidate = entityManager.persist(candidate);

        // 4. Create and Persist a Resume
        Resume resume = new Resume();
        resume.setUser(candidate);
        resume.setScore(95.0);
        resume.setExtractedText("Extensive experience in Java, Spring Boot, and SQL.");
        resume.setFilePath("/uploads/resumes/test_resume.pdf"); // FIX: Added mandatory file_path
        resume.setActive(true);
        // Add dummy values for these if your DB requires them:
        resume.setMatchedSkills("Java, Spring Boot");
        resume.setMissingSkills("Angular");

        entityManager.persist(resume);

        // 5. Create and Persist the Application
        Application application = new Application();
        application.setJob(savedJob);
        application.setUser(candidate);
        application.setStatus(ApplicationStatus.APPLIED);
        entityManager.persist(application);

        entityManager.flush();
    }

    @Test
    @DisplayName("Filter candidates should return correct application for specific skill")
    void testFilterCandidates_Success() {
        // Act
        List<Application> results = applicationRepository.filterCandidates(
                savedJob.getId(),
                ApplicationStatus.APPLIED,
                90.0,
                "Java"
        );

        // Assert
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getUser().getEmail()).isEqualTo("ranjith.candidate@test.com");
    }

    @Test
    @DisplayName("Filter candidates should return empty if score is below minScore")
    void testFilterCandidates_LowScore() {
        // Act: Searching for 99 score when candidate has 95
        List<Application> results = applicationRepository.filterCandidates(
                savedJob.getId(),
                ApplicationStatus.APPLIED,
                99.0,
                "Java"
        );

        // Assert
        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("Count by Job should return correct number of applicants")
    void testCountByJob() {
        long count = applicationRepository.countByJob(savedJob);
        assertThat(count).isEqualTo(1);
    }
}