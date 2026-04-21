package com.hiresmarter.jobportal.repository;


import com.hiresmarter.jobportal.entity.Application;
import com.hiresmarter.jobportal.entity.Job;
import com.hiresmarter.jobportal.entity.User;
import com.hiresmarter.jobportal.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    Optional<Application> findByUserAndJob(User user, Job job);

    List<Application> findByUser(User user);

    List<Application> findByJob(Job job);

    List<Application> findByStatus(ApplicationStatus status);

    long countByJob(Job job);
    long countByStatus(ApplicationStatus status);

    @Query("""
    SELECT a FROM Application a
    JOIN a.user u
    JOIN Resume r ON r.user = u
    WHERE a.job.id = :jobId
      AND r.isActive = true
      AND (:status IS NULL OR a.status = :status)
      AND (:minScore IS NULL OR r.score >= :minScore)
      AND (:skill IS NULL OR LOWER(CAST(r.extractedText AS string)) LIKE LOWER(CONCAT('%', :skill, '%')))
      """)
    List<Application> filterCandidates(
            @Param("jobId") Long jobId,
            @Param("status") ApplicationStatus status,
            @Param("minScore") Double minScore,
            @Param("skill") String skill
    );
}