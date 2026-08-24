package com.recruitment.recruitmentplatform.repository;

import com.recruitment.recruitmentplatform.entity.InterviewFeedback;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterviewFeedbackRepository
        extends JpaRepository<InterviewFeedback, Long> {

    /*
     * Get all feedback records for an application.
     */
    List<InterviewFeedback>
    findByApplicationIdOrderByCreatedAtDesc(
            Long applicationId
    );

    /*
     * Get feedback submitted by one interviewer.
     */
    List<InterviewFeedback>
    findByInterviewerIdOrderByCreatedAtDesc(
            Long interviewerId
    );
}