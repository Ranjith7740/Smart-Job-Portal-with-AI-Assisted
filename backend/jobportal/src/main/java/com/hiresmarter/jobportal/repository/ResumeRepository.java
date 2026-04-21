package com.hiresmarter.jobportal.repository;


import com.hiresmarter.jobportal.entity.Resume;
import com.hiresmarter.jobportal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ResumeRepository extends JpaRepository<Resume, Long> {

    List<Resume> findByUser(User user);

    Optional<Resume> findByUserAndIsActiveTrue(User user);
    @Query("SELECT AVG(r.score) FROM Resume r WHERE r.score IS NOT NULL")
    Double getGlobalAverageScore();
}
