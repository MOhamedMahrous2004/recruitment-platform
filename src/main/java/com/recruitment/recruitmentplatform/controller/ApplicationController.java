package com.recruitment.recruitmentplatform.controller;

import com.recruitment.recruitmentplatform.entity.Application;
import com.recruitment.recruitmentplatform.entity.ApplicationStatus;
import com.recruitment.recruitmentplatform.entity.ApplicationStatusHistory;
import com.recruitment.recruitmentplatform.entity.InterviewFeedback;
import com.recruitment.recruitmentplatform.service.ApplicationService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    // =============================================
    // CANDIDATE
    // =============================================

    @PostMapping("/candidate/applications/apply")
    public ResponseEntity<Application> applyToJob(
            @RequestParam Long jobId,
            Authentication authentication) {

        Application application = applicationService.applyToJob(
                authentication.getName(),
                jobId
        );

        return ResponseEntity.ok(application);
    }

    @GetMapping("/candidate/applications")
    public ResponseEntity<Page<Application>> getMyApplications(
            Authentication authentication,
            @PageableDefault(size = 10, sort = "appliedAt", direction = Sort.Direction.DESC)
            Pageable pageable) {

        return ResponseEntity.ok(
                applicationService.getMyApplications(
                        authentication.getName(),
                        pageable
                )
        );
    }

    // =============================================
    // HR
    // =============================================

    @GetMapping("/hr/applications")
    public ResponseEntity<Page<Application>> getAllApplications(
            @PageableDefault(size = 10, sort = "appliedAt", direction = Sort.Direction.DESC)
            Pageable pageable) {

        return ResponseEntity.ok(
                applicationService.getAllApplications(pageable)
        );
    }

    @GetMapping("/hr/applications/{id}")
    public ResponseEntity<Application> getApplicationById(@PathVariable Long id) {
        return ResponseEntity.ok(applicationService.getApplicationById(id));
    }

    @GetMapping("/hr/applications/job/{jobId}")
    public ResponseEntity<Page<Application>> getApplicationsByJob(
            @PathVariable Long jobId,
            @PageableDefault(size = 10, sort = "appliedAt", direction = Sort.Direction.DESC)
            Pageable pageable) {

        return ResponseEntity.ok(
                applicationService.getApplicationsByJob(jobId, pageable)
        );
    }

    @GetMapping("/hr/applications/status/{status}")
    public ResponseEntity<Page<Application>> getApplicationsByStatus(
            @PathVariable ApplicationStatus status,
            @PageableDefault(size = 10, sort = "appliedAt", direction = Sort.Direction.DESC)
            Pageable pageable) {

        return ResponseEntity.ok(
                applicationService.getApplicationsByStatus(status, pageable)
        );
    }

    @PutMapping("/hr/applications/{id}/status")
    public ResponseEntity<Application> updateStatus(
            @PathVariable Long id,
            @RequestParam ApplicationStatus status,
            Authentication authentication) {

        return ResponseEntity.ok(
                applicationService.updateStatus(id, status, authentication.getName())
        );
    }

    @GetMapping("/hr/applications/{id}/history")
    public ResponseEntity<List<ApplicationStatusHistory>> getApplicationStatusHistory(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                applicationService.getApplicationStatusHistory(id)
        );
    }

    @GetMapping("/hr/applications/{id}/feedback")
    public ResponseEntity<List<InterviewFeedback>> getApplicationFeedback(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                applicationService.getApplicationFeedback(id)
        );
    }

    @PutMapping("/hr/applications/{id}/recruiter")
    public ResponseEntity<Application> assignRecruiter(
            @PathVariable Long id,
            @RequestParam Long userId) {

        return ResponseEntity.ok(
                applicationService.assignRecruiter(id, userId)
        );
    }

    @PutMapping("/hr/applications/{id}/interviewer")
    public ResponseEntity<Application> assignInterviewer(
            @PathVariable Long id,
            @RequestParam Long userId) {

        return ResponseEntity.ok(
                applicationService.assignInterviewer(id, userId)
        );
    }

    // =============================================
    // ADMIN
    // =============================================

    @GetMapping("/admin/applications")
    public ResponseEntity<Page<Application>> getAllApplicationsAsAdmin(
            @PageableDefault(size = 10, sort = "appliedAt", direction = Sort.Direction.DESC)
            Pageable pageable) {

        return ResponseEntity.ok(
                applicationService.getAllApplications(pageable)
        );
    }

    @GetMapping("/admin/applications/{id}")
    public ResponseEntity<Application> getApplicationByIdAsAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(applicationService.getApplicationById(id));
    }

    @GetMapping("/admin/applications/job/{jobId}")
    public ResponseEntity<Page<Application>> getApplicationsByJobAsAdmin(
            @PathVariable Long jobId,
            @PageableDefault(size = 10, sort = "appliedAt", direction = Sort.Direction.DESC)
            Pageable pageable) {

        return ResponseEntity.ok(
                applicationService.getApplicationsByJob(jobId, pageable)
        );
    }

    @GetMapping("/admin/applications/status/{status}")
    public ResponseEntity<Page<Application>> getApplicationsByStatusAsAdmin(
            @PathVariable ApplicationStatus status,
            @PageableDefault(size = 10, sort = "appliedAt", direction = Sort.Direction.DESC)
            Pageable pageable) {

        return ResponseEntity.ok(
                applicationService.getApplicationsByStatus(status, pageable)
        );
    }

    @PutMapping("/admin/applications/{id}/status")
    public ResponseEntity<Application> updateStatusAsAdmin(
            @PathVariable Long id,
            @RequestParam ApplicationStatus status,
            Authentication authentication) {

        return ResponseEntity.ok(
                applicationService.updateStatus(id, status, authentication.getName())
        );
    }

    @GetMapping("/admin/applications/{id}/history")
    public ResponseEntity<List<ApplicationStatusHistory>> getApplicationStatusHistoryAsAdmin(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                applicationService.getApplicationStatusHistory(id)
        );
    }

    @GetMapping("/admin/applications/{id}/feedback")
    public ResponseEntity<List<InterviewFeedback>> getApplicationFeedbackAsAdmin(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                applicationService.getApplicationFeedback(id)
        );
    }

    @PutMapping("/admin/applications/{id}/recruiter")
    public ResponseEntity<Application> assignRecruiterAsAdmin(
            @PathVariable Long id,
            @RequestParam Long userId) {

        return ResponseEntity.ok(
                applicationService.assignRecruiter(id, userId)
        );
    }

    @PutMapping("/admin/applications/{id}/interviewer")
    public ResponseEntity<Application> assignInterviewerAsAdmin(
            @PathVariable Long id,
            @RequestParam Long userId) {

        return ResponseEntity.ok(
                applicationService.assignInterviewer(id, userId)
        );
    }

    // =============================================
    // INTERVIEWER
    // =============================================

    @PostMapping("/interviewer/applications/{id}/feedback")
    public ResponseEntity<InterviewFeedback> submitFeedback(
            @PathVariable Long id,
            @RequestParam Integer evaluationScore,
            @RequestParam String feedback,
            Authentication authentication) {

        return ResponseEntity.ok(
                applicationService.submitFeedback(
                        id,
                        authentication.getName(),
                        evaluationScore,
                        feedback
                )
        );
    }

    @GetMapping("/interviewer/feedback")
    public ResponseEntity<List<InterviewFeedback>> getMyFeedback(
            Authentication authentication) {

        return ResponseEntity.ok(
                applicationService.getInterviewerFeedback(
                        authentication.getName()
                )
        );
    }
}