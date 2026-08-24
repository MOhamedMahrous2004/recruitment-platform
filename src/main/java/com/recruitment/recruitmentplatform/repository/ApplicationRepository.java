package com.recruitment.recruitmentplatform.repository;

import com.recruitment.recruitmentplatform.entity.Application;
import com.recruitment.recruitmentplatform.entity.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository
        extends JpaRepository<Application, Long> {

    /*
     * Check if candidate already applied to the same job.
     */
    boolean existsByCandidateIdAndJobId(
            Long candidateId,
            Long jobId
    );

    /*
     * ✅ Get applications of one candidate (with Pagination).
     */
    Page<Application>
    findByCandidateIdOrderByAppliedAtDesc(
            Long candidateId,
            Pageable pageable
    );

    /*
     * ✅ Get applications for one job (with Pagination).
     */
    Page<Application>
    findByJobIdOrderByAppliedAtDesc(
            Long jobId,
            Pageable pageable
    );

    /*
     * ✅ Get applications by status (with Pagination).
     */
    Page<Application>
    findByStatusOrderByAppliedAtDesc(
            ApplicationStatus status,
            Pageable pageable
    );
}