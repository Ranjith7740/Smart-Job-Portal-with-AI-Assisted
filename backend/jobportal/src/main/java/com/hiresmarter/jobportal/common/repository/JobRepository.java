package com.hiresmarter.jobportal.common.repository;

import com.hiresmarter.jobportal.common.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Long> {
}