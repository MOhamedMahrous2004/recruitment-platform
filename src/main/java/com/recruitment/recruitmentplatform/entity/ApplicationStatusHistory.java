package com.recruitment.recruitmentplatform.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "application_status_history")
public class ApplicationStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Application whose status changed.
     */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "application_id",
            nullable = false
    )
    private Application application;

    /*
     * Previous status.
     *
     * Null when the history record represents
     * the initial application creation.
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private ApplicationStatus oldStatus;

    /*
     * New status.
     */
    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private ApplicationStatus newStatus;

    /*
     * Email of the user who changed the status.
     *
     * We store the email rather than exposing the
     * complete User entity in the API response.
     */
    @Column(
            nullable = false,
            length = 255
    )
    private String changedBy;

    /*
     * Date and time of the change.
     */
    @Column(nullable = false)
    private LocalDateTime changedAt;

    public ApplicationStatusHistory() {
    }

    public ApplicationStatusHistory(
            Application application,
            ApplicationStatus oldStatus,
            ApplicationStatus newStatus,
            String changedBy,
            LocalDateTime changedAt) {

        this.application = application;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.changedBy = changedBy;
        this.changedAt = changedAt;
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

    public ApplicationStatus getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(
            ApplicationStatus oldStatus) {

        this.oldStatus = oldStatus;
    }

    public ApplicationStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(
            ApplicationStatus newStatus) {

        this.newStatus = newStatus;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(
            String changedBy) {

        this.changedBy = changedBy;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(
            LocalDateTime changedAt) {

        this.changedAt = changedAt;
    }
}