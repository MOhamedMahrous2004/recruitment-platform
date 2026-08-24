package com.recruitment.recruitmentplatform.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "applications",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_candidate_job",
                        columnNames = {
                                "candidate_id",
                                "job_id"
                        }
                )
        }
)
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Candidate who applied.
     */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(
            name = "candidate_id",
            nullable = false
    )
    private Candidate candidate;

    /*
     * Job applied for.
     */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(
            name = "job_id",
            nullable = false
    )
    private Job job;

    /*
     * Current application status.
     */
    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private ApplicationStatus status;

    /*
     * Date and time when application
     * was created.
     */
    @Column(nullable = false)
    private LocalDateTime appliedAt;

    /*
     * Recruiter responsible for the application.
     *
     * Must be an HR user.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "recruiter_id")
    private User recruiter;

    /*
     * Interviewer responsible for interviews/evaluation.
     *
     * Must be an INTERVIEWER user.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "interviewer_id")
    private User interviewer;

    public Application() {
    }

    public Application(
            Candidate candidate,
            Job job,
            ApplicationStatus status,
            LocalDateTime appliedAt) {

        this.candidate = candidate;
        this.job = job;
        this.status = status;
        this.appliedAt = appliedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(
            Candidate candidate) {

        this.candidate = candidate;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(
            ApplicationStatus status) {

        this.status = status;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(
            LocalDateTime appliedAt) {

        this.appliedAt = appliedAt;
    }

    public User getRecruiter() {
        return recruiter;
    }

    public void setRecruiter(
            User recruiter) {

        this.recruiter = recruiter;
    }

    public User getInterviewer() {
        return interviewer;
    }

    public void setInterviewer(
            User interviewer) {

        this.interviewer = interviewer;
    }
}