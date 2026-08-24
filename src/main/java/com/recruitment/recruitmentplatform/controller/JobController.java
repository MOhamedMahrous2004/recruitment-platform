package com.recruitment.recruitmentplatform.controller;

import com.recruitment.recruitmentplatform.entity.Job;
import com.recruitment.recruitmentplatform.entity.User;
import com.recruitment.recruitmentplatform.repository.UserRepository;
import com.recruitment.recruitmentplatform.service.JobService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class JobController {

    private final JobService jobService;
    private final UserRepository userRepository;

    public JobController(
            JobService jobService,
            UserRepository userRepository) {

        this.jobService = jobService;
        this.userRepository = userRepository;
    }


    /*
     * ================================
     * ALL JOBS
     * ================================
     *
     * All authenticated users
     * can view jobs.
     */
    @GetMapping("/jobs")
    public String jobs(
            Model model,
            Authentication authentication) {

        /*
         * Get all jobs.
         */
        model.addAttribute(
                "jobs",
                jobService.getAllJobs()
        );


        /*
         * Get logged-in user's role.
         *
         * Example:
         *
         * ROLE_ADMIN
         *      ↓
         * ADMIN
         *
         * ROLE_HR
         *      ↓
         * HR
         *
         * ROLE_CANDIDATE
         *      ↓
         * CANDIDATE
         */
        String role = authentication
                .getAuthorities()
                .stream()
                .findFirst()
                .map(authority ->
                        authority.getAuthority()
                )
                .orElse("");


        /*
         * Remove ROLE_ prefix.
         */
        if (role.startsWith("ROLE_")) {

            role = role.substring(5);
        }


        /*
         * Send role to Thymeleaf.
         */
        model.addAttribute(
                "role",
                role
        );


        return "jobs";
    }


    /*
     * ================================
     * CREATE JOB PAGE
     * ================================
     *
     * Accessible only to:
     *
     * ADMIN
     * HR
     */
    @GetMapping("/jobs/create")
    public String createJobPage(Model model) {

        model.addAttribute(
                "job",
                new Job()
        );

        return "create-job";
    }


    /*
     * ================================
     * CREATE JOB
     * ================================
     *
     * The logged-in ADMIN or HR
     * becomes the creator of the job.
     */
    @PostMapping("/jobs/create")
    public String createJob(
            @ModelAttribute("job") Job job,
            Authentication authentication) {


        /*
         * Get logged-in user's email.
         */
        String email =
                authentication.getName();


        /*
         * Find user in database.
         */
        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found with email: "
                                                + email
                                )
                        );


        /*
         * Create job and assign
         * the logged-in user as creator.
         */
        jobService.createJob(
                job,
                user
        );


        /*
         * Return to jobs page.
         */
        return "redirect:/jobs";
    }


    /*
     * ================================
     * JOB DETAILS
     * ================================
     *
     * All authenticated users
     * can view job details.
     */
    @GetMapping("/jobs/{id}")
    public String jobDetails(
            @PathVariable Long id,
            Model model,
            Authentication authentication) {


        /*
         * Get job.
         */
        Job job =
                jobService.getJobById(id);


        /*
         * Send job to Thymeleaf.
         */
        model.addAttribute(
                "job",
                job
        );


        /*
         * Get logged-in user's role.
         */
        String role = authentication
                .getAuthorities()
                .stream()
                .findFirst()
                .map(authority ->
                        authority.getAuthority()
                )
                .orElse("");


        /*
         * Remove ROLE_ prefix.
         */
        if (role.startsWith("ROLE_")) {

            role = role.substring(5);
        }


        /*
         * Send role to Thymeleaf.
         */
        model.addAttribute(
                "role",
                role
        );


        return "job-details";
    }


    /*
     * ================================
     * EDIT JOB PAGE
     * ================================
     *
     * Only ADMIN and HR
     * can access this page.
     */
    @GetMapping("/jobs/{id}/edit")
    public String editJobPage(
            @PathVariable Long id,
            Model model) {


        /*
         * Get existing job.
         */
        Job job =
                jobService.getJobById(id);


        /*
         * Send job to form.
         */
        model.addAttribute(
                "job",
                job
        );


        return "edit-job";
    }


    /*
     * ================================
     * UPDATE JOB
     * ================================
     *
     * Only ADMIN and HR
     * can update jobs.
     */
    @PostMapping("/jobs/{id}/edit")
    public String updateJob(
            @PathVariable Long id,
            @ModelAttribute("job") Job job) {


        /*
         * Update job.
         */
        jobService.updateJob(
                id,
                job
        );


        /*
         * Return to job details.
         */
        return "redirect:/jobs/" + id;
    }


    /*
     * ================================
     * DELETE JOB
     * ================================
     *
     * Only ADMIN and HR
     * can delete jobs.
     */
    @PostMapping("/jobs/{id}/delete")
    public String deleteJob(
            @PathVariable Long id) {


        /*
         * Delete job.
         */
        jobService.deleteJob(id);


        /*
         * Return to jobs page.
         */
        return "redirect:/jobs";
    }
}