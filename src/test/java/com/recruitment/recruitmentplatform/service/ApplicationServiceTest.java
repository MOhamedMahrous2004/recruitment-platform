package com.recruitment.recruitmentplatform.service;

import com.recruitment.recruitmentplatform.entity.Application;
import com.recruitment.recruitmentplatform.entity.ApplicationStatus;
import com.recruitment.recruitmentplatform.entity.Candidate;
import com.recruitment.recruitmentplatform.entity.Job;
import com.recruitment.recruitmentplatform.entity.User;
import com.recruitment.recruitmentplatform.repository.ApplicationRepository;
import com.recruitment.recruitmentplatform.repository.ApplicationStatusHistoryRepository;
import com.recruitment.recruitmentplatform.repository.CandidateRepository;
import com.recruitment.recruitmentplatform.repository.InterviewFeedbackRepository;
import com.recruitment.recruitmentplatform.repository.JobRepository;
import com.recruitment.recruitmentplatform.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private ApplicationStatusHistoryRepository
            applicationStatusHistoryRepository;

    @Mock
    private InterviewFeedbackRepository
            interviewFeedbackRepository;

    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ApplicationService applicationService;

    private User candidateUser;
    private Candidate candidate;
    private Job job;

    @BeforeEach
    void setUp() {

        candidateUser =
                new User(
                        "Candidate One",
                        "candidate@test.com",
                        "password",
                        "CANDIDATE"
                );

        candidateUser.setId(1L);

        candidate =
                new Candidate(
                        candidateUser,
                        "Candidate One",
                        "candidate@test.com",
                        "01000000000",
                        "Cairo"
                );

        candidate.setId(10L);

        job =
                new Job();

        job.setId(20L);

        job.setTitle(
                "Java Backend Developer"
        );
    }

    /*
     * ==========================================
     * TEST APPLY TO JOB
     * ==========================================
     */
    @Test
    void applyToJob_shouldCreateApplication() {

        when(userRepository.findByEmail(
                "candidate@test.com"
        )).thenReturn(
                Optional.of(candidateUser)
        );

        when(candidateRepository.findByUserId(
                1L
        )).thenReturn(
                Optional.of(candidate)
        );

        when(jobRepository.findById(
                20L
        )).thenReturn(
                Optional.of(job)
        );

        when(applicationRepository
                .existsByCandidateIdAndJobId(
                        10L,
                        20L
                ))
                .thenReturn(false);

        when(applicationRepository.save(
                any(Application.class)
        )).thenAnswer(invocation ->
                invocation.getArgument(0)
        );

        Application result =
                applicationService.applyToJob(
                        "candidate@test.com",
                        20L
                );

        assertNotNull(result);

        assertEquals(
                candidate,
                result.getCandidate()
        );

        assertEquals(
                job,
                result.getJob()
        );

        assertEquals(
                ApplicationStatus.APPLIED,
                result.getStatus()
        );

        assertNotNull(
                result.getAppliedAt()
        );

        verify(applicationRepository)
                .save(
                        any(Application.class)
                );

        verify(
                applicationStatusHistoryRepository
        ).save(any());
    }

    /*
     * ==========================================
     * TEST DUPLICATE APPLICATION
     * ==========================================
     */
    @Test
    void applyToJob_shouldRejectDuplicateApplication() {

        when(userRepository.findByEmail(
                "candidate@test.com"
        )).thenReturn(
                Optional.of(candidateUser)
        );

        when(candidateRepository.findByUserId(
                1L
        )).thenReturn(
                Optional.of(candidate)
        );

        when(jobRepository.findById(
                20L
        )).thenReturn(
                Optional.of(job)
        );

        when(applicationRepository
                .existsByCandidateIdAndJobId(
                        10L,
                        20L
                ))
                .thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        applicationService.applyToJob(
                                "candidate@test.com",
                                20L
                        )
        );

        verify(
                applicationRepository,
                never()
        ).save(
                any(Application.class)
        );
    }

    /*
     * ==========================================
     * TEST STATUS UPDATE
     * ==========================================
     */
    @Test
    void updateStatus_shouldChangeStatusAndCreateHistory() {

        Application application =
                new Application();

        application.setId(100L);

        application.setCandidate(
                candidate
        );

        application.setJob(
                job
        );

        application.setStatus(
                ApplicationStatus.APPLIED
        );

        when(applicationRepository.findById(
                100L
        )).thenReturn(
                Optional.of(application)
        );

        when(applicationRepository.save(
                any(Application.class)
        )).thenAnswer(invocation ->
                invocation.getArgument(0)
        );

        Application result =
                applicationService.updateStatus(
                        100L,
                        ApplicationStatus.INTERVIEW,
                        "hr@test.com"
                );

        assertEquals(
                ApplicationStatus.INTERVIEW,
                result.getStatus()
        );

        verify(applicationRepository)
                .save(application);

        verify(
                applicationStatusHistoryRepository
        ).save(any());
    }

    /*
     * ==========================================
     * TEST INVALID STATUS
     * ==========================================
     */
    @Test
    void updateStatus_shouldRejectNullStatus() {

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        applicationService.updateStatus(
                                100L,
                                null,
                                "hr@test.com"
                        )
        );

        verify(
                applicationRepository,
                never()
        ).findById(anyLong());
    }
}