package com.recruitment.recruitmentplatform.service;

import com.recruitment.recruitmentplatform.entity.Job;
import com.recruitment.recruitmentplatform.entity.User;
import com.recruitment.recruitmentplatform.repository.JobRepository;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobService {

    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    /*
     * ================================
     * GET ALL JOBS (✅ Cached)
     * ================================
     */
    @Cacheable("jobs")
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    /*
     * ================================
     * GET JOBS BY USER
     * ================================
     */
    public List<Job> getJobsByUser(Long userId) {
        return jobRepository.findByCreatedById(userId);
    }

    /*
     * ================================
     * GET JOB BY ID (✅ Cached)
     * ================================
     */
    @Cacheable(value = "jobs", key = "#id")
    public Job getJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found with ID: " + id));
    }

    /*
     * ================================
     * CREATE JOB (✅ Clear cache)
     * ================================
     */
    @CacheEvict(value = "jobs", allEntries = true)
    public Job createJob(Job job, User createdBy) {
        job.setCreatedBy(createdBy);
        return jobRepository.save(job);
    }

    /*
     * ================================
     * UPDATE JOB (✅ Clear cache)
     * ================================
     */
    @CacheEvict(value = "jobs", allEntries = true)
    public Job updateJob(Long id, Job updatedJob) {
        Job existingJob = getJobById(id);
        existingJob.setTitle(updatedJob.getTitle());
        existingJob.setDescription(updatedJob.getDescription());
        existingJob.setLocation(updatedJob.getLocation());
        existingJob.setEmploymentType(updatedJob.getEmploymentType());
        existingJob.setSkills(updatedJob.getSkills());
        existingJob.setSalary(updatedJob.getSalary());
        return jobRepository.save(existingJob);
    }

    /*
     * ================================
     * DELETE JOB (✅ Clear cache)
     * ================================
     */
    @CacheEvict(value = "jobs", allEntries = true)
    public void deleteJob(Long id) {
        if (!jobRepository.existsById(id)) {
            throw new RuntimeException("Job not found with ID: " + id);
        }
        jobRepository.deleteById(id);
    }
}