package com.recruitment.recruitmentplatform.repository;

import com.recruitment.recruitmentplatform.entity.ApplicationStatusHistory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationStatusHistoryRepository
        extends JpaRepository<
        ApplicationStatusHistory,
        Long> {

    /*
     * Get complete history of one application.
     */
    List<ApplicationStatusHistory>
    findByApplicationIdOrderByChangedAtAsc(
            Long applicationId
    );
}