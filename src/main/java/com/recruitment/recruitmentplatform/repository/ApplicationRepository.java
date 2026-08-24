package com.recruitment.recruitmentplatform.repository;

import com.recruitment.recruitmentplatform.entity.Application;
import com.recruitment.recruitmentplatform.entity.ApplicationStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository
        extends JpaRepository<Application, Long> {

    /*
     * Check if candidate already applied
     * to the same job.
     */
    boolean existsByCandidateIdAndJobId(
            Long candidateId,
            Long jobId
    );

    /*
     * Get applications of one candidate.
     */
    List<Application>
    findByCandidateIdOrderByAppliedAtDesc(
            Long candidateId
    );

    /*
     * Get applications for one job.
     */
    List<Application>
    findByJobIdOrderByAppliedAtDesc(
            Long jobId
    );

    /*
     * Get applications by status.
     */
    List<Application>
    findByStatusOrderByAppliedAtDesc(
            ApplicationStatus status
    );
}