package com.hiresmarter.jobportal.repository;

import com.hiresmarter.jobportal.entity.Job;
import com.hiresmarter.jobportal.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Long> {

    Page<Job> findAll(Pageable pageable);

    Page<Job> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<Job> findByRecruiter(User recruiter, Pageable pageable);


}