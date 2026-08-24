package com.recruitment.recruitmentplatform.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String employmentType;

    @Column(nullable = false)
    private String skills;

    @Column(nullable = false)
    private String salary;

    /*
     * User who created the job.
     *
     * ADMIN or HR.
     *
     * This relation is not returned in the JSON response
     * to prevent exposing user information.
     */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(
            name = "created_by_id",
            nullable = false
    )
    private User createdBy;

    public Job() {
    }

    public Job(
            String title,
            String description,
            String location,
            String employmentType,
            String skills,
            String salary,
            User createdBy) {

        this.title = title;
        this.description = description;
        this.location = location;
        this.employmentType = employmentType;
        this.skills = skills;
        this.salary = salary;
        this.createdBy = createdBy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(
            String employmentType) {

        this.employmentType = employmentType;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }
}