package com.recruitment.recruitmentplatform.repository;

import com.recruitment.recruitmentplatform.entity.Job;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByCreatedById(Long userId);

}