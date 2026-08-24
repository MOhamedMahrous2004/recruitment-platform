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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

        user =
                new User(
                        "Candidate One",
                        "candidate@test.com",
                        "password",
                        "CANDIDATE"
                );

        user.setId(1L);

        candidate =
                new Candidate(
                        user,
                        "Candidate One",
                        "candidate@test.com",
                        "01000000000",
                        "Cairo"
                );

        candidate.setId(10L);
    }

    /*
     * ==========================================
     * TEST CANDIDATE SEARCH
     * ==========================================
     */
    @Test
    void searchCandidates_shouldReturnMatchingCandidates() {

        when(
                candidateRepository
                        .findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrTagsContainingIgnoreCase(
                                "Java",
                                "Java",
                                "Java"
                        )
        ).thenReturn(
                List.of(candidate)
        );

        List<Candidate> result =
                candidateService.searchCandidates(
                        "Java"
                );

        assertEquals(
                1,
                result.size()
        );

        assertEquals(
                candidate,
                result.get(0)
        );
    }

    /*
     * ==========================================
     * TEST SEARCH WITHOUT FILTER
     * ==========================================
     */
    @Test
    void searchCandidates_withoutSearchShouldReturnAll() {

        when(
                candidateRepository.findAll()
        ).thenReturn(
                List.of(candidate)
        );

        List<Candidate> result =
                candidateService.searchCandidates(
                        null
                );

        assertEquals(
                1,
                result.size()
        );

        verify(
                candidateRepository
        ).findAll();
    }

    /*
     * ==========================================
     * TEST GET CANDIDATE BY ID
     * ==========================================
     */
    @Test
    void getCandidateById_shouldReturnCandidate() {

        when(
                candidateRepository.findById(10L)
        ).thenReturn(
                Optional.of(candidate)
        );

        Candidate result =
                candidateService.getCandidateById(
                        10L
                );

        assertNotNull(result);

        assertEquals(
                10L,
                result.getId()
        );

        assertEquals(
                "Candidate One",
                result.getFullName()
        );
    }

    /*
     * ==========================================
     * TEST CANDIDATE NOT FOUND
     * ==========================================
     */
    @Test
    void getCandidateById_shouldThrowWhenNotFound() {

        when(
                candidateRepository.findById(999L)
        ).thenReturn(
                Optional.empty()
        );

        assertThrows(
                RuntimeException.class,
                () ->
                        candidateService
                                .getCandidateById(
                                        999L
                                )
        );
    }
}