package com.recruitment.recruitmentplatform.service;

import com.recruitment.recruitmentplatform.entity.Candidate;
import com.recruitment.recruitmentplatform.entity.User;
import com.recruitment.recruitmentplatform.repository.CandidateRepository;
import com.recruitment.recruitmentplatform.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CandidateServiceTest {

    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CvParsingService cvParsingService;

    @InjectMocks
    private CandidateService candidateService;

    private User user;
    private Candidate candidate;

    @BeforeEach
    void setUp() {
        user = new User("Candidate One", "candidate@test.com", "password", "CANDIDATE");
        user.setId(1L);

        candidate = new Candidate(user, "Candidate One", "candidate@test.com", "01000000000", "Cairo");
        candidate.setId(10L);
    }

    // ==========================================
    // TEST CANDIDATE SEARCH (with Pagination)
    // ==========================================
    @Test
    void searchCandidates_shouldReturnMatchingCandidates() {
        // ✅ Mock the repository with Pageable
        Page<Candidate> page = new PageImpl<>(List.of(candidate));
        when(candidateRepository.findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrTagsContainingIgnoreCase(
                eq("Java"), eq("Java"), eq("Java"), any(Pageable.class)
        )).thenReturn(page);

        // ✅ Call service with Pageable.unpaged()
        Page<Candidate> result = candidateService.searchCandidates("Java", Pageable.unpaged());

        assertEquals(1, result.getContent().size());
        assertEquals(candidate, result.getContent().get(0));
    }

    // ==========================================
    // TEST SEARCH WITHOUT FILTER
    // ==========================================
    @Test
    void searchCandidates_withoutSearchShouldReturnAll() {
        // ✅ Mock findAll with Pageable
        Page<Candidate> page = new PageImpl<>(List.of(candidate));
        when(candidateRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<Candidate> result = candidateService.searchCandidates(null, Pageable.unpaged());

        assertEquals(1, result.getContent().size());
        verify(candidateRepository).findAll(any(Pageable.class));
    }

    // ==========================================
    // TEST GET CANDIDATE BY ID
    // ==========================================
    @Test
    void getCandidateById_shouldReturnCandidate() {
        when(candidateRepository.findById(10L)).thenReturn(Optional.of(candidate));

        Candidate result = candidateService.getCandidateById(10L);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("Candidate One", result.getFullName());
    }

    // ==========================================
    // TEST CANDIDATE NOT FOUND
    // ==========================================
    @Test
    void getCandidateById_shouldThrowWhenNotFound() {
        when(candidateRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> candidateService.getCandidateById(999L));
    }
}