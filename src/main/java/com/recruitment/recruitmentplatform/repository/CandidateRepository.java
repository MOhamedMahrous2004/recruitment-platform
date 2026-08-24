package com.recruitment.recruitmentplatform.repository;

import com.recruitment.recruitmentplatform.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CandidateRepository
        extends JpaRepository<Candidate, Long> {

    /*
     * Find Candidate profile using the linked User ID.
     */
    Optional<Candidate> findByUserId(Long userId);

    /*
     * Search Candidates by:
     *
     * - Full name
     * - Email
     * - Tags
     */
    List<Candidate>
    findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrTagsContainingIgnoreCase(
            String fullName,
            String email,
            String tags
    );
}