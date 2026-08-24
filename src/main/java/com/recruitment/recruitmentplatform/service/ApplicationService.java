package com.recruitment.recruitmentplatform.service;

import com.recruitment.recruitmentplatform.entity.Application;
import com.recruitment.recruitmentplatform.entity.ApplicationStatus;
import com.recruitment.recruitmentplatform.entity.ApplicationStatusHistory;
import com.recruitment.recruitmentplatform.entity.Candidate;
import com.recruitment.recruitmentplatform.entity.InterviewFeedback;
import com.recruitment.recruitmentplatform.entity.Job;
import com.recruitment.recruitmentplatform.entity.User;

import com.recruitment.recruitmentplatform.repository.ApplicationRepository;
import com.recruitment.recruitmentplatform.repository.ApplicationStatusHistoryRepository;
import com.recruitment.recruitmentplatform.repository.CandidateRepository;
import com.recruitment.recruitmentplatform.repository.InterviewFeedbackRepository;
import com.recruitment.recruitmentplatform.repository.JobRepository;
import com.recruitment.recruitmentplatform.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // ★ تم إضافة هذا الاستيراد

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional // ★ تم إضافة هذه التعليمة: تضمن أن كل العمليات إما تنجح كلها أو ترجع كما كانت
public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    private final ApplicationStatusHistoryRepository
            applicationStatusHistoryRepository;

    private final InterviewFeedbackRepository
            interviewFeedbackRepository;

    private final CandidateRepository candidateRepository;

    private final JobRepository jobRepository;

    private final UserRepository userRepository;

    public ApplicationService(
            ApplicationRepository applicationRepository,
            ApplicationStatusHistoryRepository
                    applicationStatusHistoryRepository,
            InterviewFeedbackRepository
                    interviewFeedbackRepository,
            CandidateRepository candidateRepository,
            JobRepository jobRepository,
            UserRepository userRepository) {

        this.applicationRepository =
                applicationRepository;

        this.applicationStatusHistoryRepository =
                applicationStatusHistoryRepository;

        this.interviewFeedbackRepository =
                interviewFeedbackRepository;

        this.candidateRepository =
                candidateRepository;

        this.jobRepository =
                jobRepository;

        this.userRepository =
                userRepository;
    }

    /*
     * ==========================================
     * APPLY TO JOB
     * ==========================================
     */
    public Application applyToJob(
            String email,
            Long jobId) {

        User user =
                getUserByEmail(email);

        if (!"CANDIDATE".equalsIgnoreCase(
                user.getRole())) {

            throw new IllegalArgumentException(
                    "Only CANDIDATE users can apply for jobs"
            );
        }

        Candidate candidate =
                candidateRepository
                        .findByUserId(user.getId())
                        .orElseGet(() ->
                                createCandidate(user)
                        );

        Job job =
                jobRepository
                        .findById(jobId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Job not found with ID: "
                                                + jobId
                                )
                        );

        if (applicationRepository
                .existsByCandidateIdAndJobId(
                        candidate.getId(),
                        job.getId()
                )) {

            throw new IllegalArgumentException(
                    "You have already applied to this job"
            );
        }

        Application application =
                new Application();

        application.setCandidate(
                candidate
        );

        application.setJob(
                job
        );

        application.setStatus(
                ApplicationStatus.APPLIED
        );

        application.setAppliedAt(
                LocalDateTime.now()
        );

        Application savedApplication =
                applicationRepository.save(
                        application
                );

        saveStatusHistory(
                savedApplication,
                null,
                ApplicationStatus.APPLIED,
                email
        );

        return savedApplication;
    }

    /*
     * ==========================================
     * GET MY APPLICATIONS
     * ==========================================
     */
    public List<Application> getMyApplications(
            String email) {

        User user =
                getUserByEmail(email);

        Candidate candidate =
                candidateRepository
                        .findByUserId(user.getId())
                        .orElseGet(() ->
                                createCandidate(user)
                        );

        return applicationRepository
                .findByCandidateIdOrderByAppliedAtDesc(
                        candidate.getId()
                );
    }

    /*
     * ==========================================
     * GET ALL APPLICATIONS
     * ==========================================
     */
    public List<Application> getAllApplications() {

        return applicationRepository.findAll();
    }

    /*
     * ==========================================
     * GET APPLICATION BY ID
     * ==========================================
     */
    public Application getApplicationById(
            Long id) {

        return applicationRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Application not found with ID: "
                                        + id
                        )
                );
    }

    /*
     * ==========================================
     * GET APPLICATIONS BY JOB
     * ==========================================
     */
    public List<Application> getApplicationsByJob(
            Long jobId) {

        if (!jobRepository.existsById(
                jobId
        )) {

            throw new RuntimeException(
                    "Job not found with ID: "
                            + jobId
            );
        }

        return applicationRepository
                .findByJobIdOrderByAppliedAtDesc(
                        jobId
                );
    }

    /*
     * ==========================================
     * GET APPLICATIONS BY STATUS
     * ==========================================
     */
    public List<Application> getApplicationsByStatus(
            ApplicationStatus status) {

        if (status == null) {

            throw new IllegalArgumentException(
                    "Status is required"
            );
        }

        return applicationRepository
                .findByStatusOrderByAppliedAtDesc(
                        status
                );
    }

    /*
     * ==========================================
     * UPDATE APPLICATION STATUS
     * ==========================================
     */
    public Application updateStatus(
            Long applicationId,
            ApplicationStatus status,
            String changedBy) {

        if (status == null) {

            throw new IllegalArgumentException(
                    "Application status is required"
            );
        }

        if (!hasText(changedBy)) {

            throw new IllegalArgumentException(
                    "Changed-by user is required"
            );
        }

        Application application =
                getApplicationById(
                        applicationId
                );

        ApplicationStatus oldStatus =
                application.getStatus();

        if (oldStatus == status) {

            return application;
        }

        application.setStatus(
                status
        );

        Application savedApplication =
                applicationRepository.save(
                        application
                );

        saveStatusHistory(
                savedApplication,
                oldStatus,
                status,
                changedBy
        );

        return savedApplication;
    }

    /*
     * ==========================================
     * GET STATUS HISTORY
     * ==========================================
     */
    public List<ApplicationStatusHistory>
    getApplicationStatusHistory(
            Long applicationId) {

        getApplicationById(
                applicationId
        );

        return applicationStatusHistoryRepository
                .findByApplicationIdOrderByChangedAtAsc(
                        applicationId
                );
    }

    /*
     * ==========================================
     * ASSIGN RECRUITER
     * ==========================================
     *
     * Recruiter must have role HR.
     */
    public Application assignRecruiter(
            Long applicationId,
            Long recruiterId) {

        Application application =
                getApplicationById(
                        applicationId
                );

        User recruiter =
                userRepository
                        .findById(recruiterId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Recruiter not found with ID: "
                                                + recruiterId
                                )
                        );

        if (!"HR".equalsIgnoreCase(
                recruiter.getRole()
        )) {

            throw new IllegalArgumentException(
                    "Assigned recruiter must have HR role"
            );
        }

        application.setRecruiter(
                recruiter
        );

        return applicationRepository.save(
                application
        );
    }

    /*
     * ==========================================
     * ASSIGN INTERVIEWER
     * ==========================================
     *
     * Interviewer must have role INTERVIEWER.
     */
    public Application assignInterviewer(
            Long applicationId,
            Long interviewerId) {

        Application application =
                getApplicationById(
                        applicationId
                );

        User interviewer =
                userRepository
                        .findById(interviewerId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Interviewer not found with ID: "
                                                + interviewerId
                                )
                        );

        if (!"INTERVIEWER".equalsIgnoreCase(
                interviewer.getRole()
        )) {

            throw new IllegalArgumentException(
                    "Assigned interviewer must have INTERVIEWER role"
            );
        }

        application.setInterviewer(
                interviewer
        );

        return applicationRepository.save(
                application
        );
    }

    /*
     * ==========================================
     * SUBMIT INTERVIEW FEEDBACK
     * ==========================================
     *
     * Only the assigned interviewer can
     * submit feedback.
     */
    public InterviewFeedback submitFeedback(
            Long applicationId,
            String interviewerEmail,
            Integer evaluationScore,
            String feedback) {

        if (evaluationScore == null) {

            throw new IllegalArgumentException(
                    "Evaluation score is required"
            );
        }

        if (evaluationScore < 0 ||
                evaluationScore > 10) {

            throw new IllegalArgumentException(
                    "Evaluation score must be between 0 and 10"
            );
        }

        if (!hasText(feedback)) {

            throw new IllegalArgumentException(
                    "Feedback is required"
            );
        }

        Application application =
                getApplicationById(
                        applicationId
                );

        if (application.getInterviewer() == null) {

            throw new IllegalArgumentException(
                    "No interviewer is assigned to this application"
            );
        }

        if (!application.getInterviewer()
                .getEmail()
                .equalsIgnoreCase(
                        interviewerEmail
                )) {

            throw new IllegalArgumentException(
                    "You are not the assigned interviewer for this application"
            );
        }

        User interviewer =
                getUserByEmail(
                        interviewerEmail
                );

        if (!"INTERVIEWER".equalsIgnoreCase(
                interviewer.getRole()
        )) {

            throw new IllegalArgumentException(
                    "Only INTERVIEWER users can submit interview feedback"
            );
        }

        InterviewFeedback feedbackEntity =
                new InterviewFeedback();

        feedbackEntity.setApplication(
                application
        );

        feedbackEntity.setInterviewer(
                interviewer
        );

        feedbackEntity.setEvaluationScore(
                evaluationScore
        );

        feedbackEntity.setFeedback(
                feedback.trim()
        );

        feedbackEntity.setCreatedAt(
                LocalDateTime.now()
        );

        return interviewFeedbackRepository.save(
                feedbackEntity
        );
    }

    /*
     * ==========================================
     * GET APPLICATION FEEDBACK
     * ==========================================
     */
    public List<InterviewFeedback>
    getApplicationFeedback(
            Long applicationId) {

        getApplicationById(
                applicationId
        );

        return interviewFeedbackRepository
                .findByApplicationIdOrderByCreatedAtDesc(
                        applicationId
                );
    }

    /*
     * ==========================================
     * GET INTERVIEWER FEEDBACK
     * ==========================================
     */
    public List<InterviewFeedback>
    getInterviewerFeedback(
            String interviewerEmail) {

        User interviewer =
                getUserByEmail(
                        interviewerEmail
                );

        if (!"INTERVIEWER".equalsIgnoreCase(
                interviewer.getRole()
        )) {

            throw new IllegalArgumentException(
                    "User is not an INTERVIEWER"
            );
        }

        return interviewFeedbackRepository
                .findByInterviewerIdOrderByCreatedAtDesc(
                        interviewer.getId()
                );
    }

    /*
     * ==========================================
     * SAVE STATUS HISTORY
     * ==========================================
     */
    private void saveStatusHistory(
            Application application,
            ApplicationStatus oldStatus,
            ApplicationStatus newStatus,
            String changedBy) {

        ApplicationStatusHistory history =
                new ApplicationStatusHistory();

        history.setApplication(
                application
        );

        history.setOldStatus(
                oldStatus
        );

        history.setNewStatus(
                newStatus
        );

        history.setChangedBy(
                changedBy
        );

        history.setChangedAt(
                LocalDateTime.now()
        );

        applicationStatusHistoryRepository.save(
                history
        );
    }

    /*
     * ==========================================
     * FIND USER
     * ==========================================
     */
    private User getUserByEmail(
            String email) {

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found with email: "
                                        + email
                        )
                );
    }

    /*
     * ==========================================
     * CREATE CANDIDATE
     * ==========================================
     */
    private Candidate createCandidate(
            User user) {

        Candidate candidate =
                new Candidate(
                        user,
                        user.getName(),
                        user.getEmail(),
                        null,
                        null
                );

        return candidateRepository.save(
                candidate
        );
    }

    /*
     * ==========================================
     * TEXT VALIDATION
     * ==========================================
     */
    private boolean hasText(
            String value) {

        return value != null &&
                !value.trim().isEmpty();
    }
}