package com.recruitment.recruitmentplatform.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "interview_feedback")
public class InterviewFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Application being evaluated.
     */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "application_id",
            nullable = false
    )
    private Application application;

    /*
     * Interviewer who submitted the feedback.
     */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(
            name = "interviewer_id",
            nullable = false
    )
    private User interviewer;

    /*
     * Evaluation score from 0 to 10.
     */
    @Column(nullable = false)
    private Integer evaluationScore;

    /*
     * Interview feedback/comments.
     */
    @Column(
            nullable = false,
            length = 5000
    )
    private String feedback;

    /*
     * Date and time of evaluation.
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    public InterviewFeedback() {
    }

    public InterviewFeedback(
            Application application,
            User interviewer,
            Integer evaluationScore,
            String feedback,
            LocalDateTime createdAt) {

        this.application = application;
        this.interviewer = interviewer;
        this.evaluationScore = evaluationScore;
        this.feedback = feedback;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(
            Application application) {

        this.application = application;
    }

    public User getInterviewer() {
        return interviewer;
    }

    public void setInterviewer(
            User interviewer) {

        this.interviewer = interviewer;
    }

    public Integer getEvaluationScore() {
        return evaluationScore;
    }

    public void setEvaluationScore(
            Integer evaluationScore) {

        this.evaluationScore = evaluationScore;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(
            String feedback) {

        this.feedback = feedback;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt) {

        this.createdAt = createdAt;
    }
}