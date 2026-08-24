package com.recruitment.recruitmentplatform.repository;

import com.recruitment.recruitmentplatform.entity.Candidate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CandidateRepository
        extends JpaRepository<Candidate, Long> {

    /*
     * Find Candidate profile using the linked User ID.
     */
    Optional<Candidate> findByUserId(Long userId);

    /*
     * Search Candidates by:
     * - Full name
     * - Email
     * - Tags
     *
     * ✅ Added Pageable for Pagination
     */
    Page<Candidate>
    findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrTagsContainingIgnoreCase(
            String fullName,
            String email,
            String tags,
            Pageable pageable
    );
}