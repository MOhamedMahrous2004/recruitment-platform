package com.recruitment.recruitmentplatform.controller;

import com.recruitment.recruitmentplatform.entity.Application;
import com.recruitment.recruitmentplatform.entity.ApplicationStatus;
import com.recruitment.recruitmentplatform.entity.ApplicationStatusHistory;
import com.recruitment.recruitmentplatform.entity.InterviewFeedback;
import com.recruitment.recruitmentplatform.service.ApplicationService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(
            ApplicationService applicationService) {

        this.applicationService =
                applicationService;
    }

    /*
     * ==========================================
     * CANDIDATE APPLY TO JOB
     * ==========================================
     */
    @PostMapping(
            "/candidate/applications/apply"
    )
    public ResponseEntity<Application>
    applyToJob(
            @RequestParam Long jobId,
            Authentication authentication) {

        Application application =
                applicationService.applyToJob(
                        authentication.getName(),
                        jobId
                );

        return ResponseEntity.ok(
                application
        );
    }

    /*
     * ==========================================
     * CANDIDATE MY APPLICATIONS
     * ==========================================
     */
    @GetMapping(
            "/candidate/applications"
    )
    public ResponseEntity<List<Application>>
    getMyApplications(
            Authentication authentication) {

        return ResponseEntity.ok(
                applicationService
                        .getMyApplications(
                                authentication.getName()
                        )
        );
    }

    /*
     * ==========================================
     * HR GET ALL APPLICATIONS
     * ==========================================
     */
    @GetMapping(
            "/hr/applications"
    )
    public ResponseEntity<List<Application>>
    getAllApplications() {

        return ResponseEntity.ok(
                applicationService
                        .getAllApplications()
        );
    }

    /*
     * ==========================================
     * ADMIN GET ALL APPLICATIONS
     * ==========================================
     */
    @GetMapping(
            "/admin/applications"
    )
    public ResponseEntity<List<Application>>
    getAllApplicationsAsAdmin() {

        return ResponseEntity.ok(
                applicationService
                        .getAllApplications()
        );
    }

    /*
     * ==========================================
     * HR GET APPLICATION BY ID
     * ==========================================
     */
    @GetMapping(
            "/hr/applications/{id}"
    )
    public ResponseEntity<Application>
    getApplicationById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                applicationService
                        .getApplicationById(id)
        );
    }

    /*
     * ==========================================
     * ADMIN GET APPLICATION BY ID
     * ==========================================
     */
    @GetMapping(
            "/admin/applications/{id}"
    )
    public ResponseEntity<Application>
    getApplicationByIdAsAdmin(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                applicationService
                        .getApplicationById(id)
        );
    }

    /*
     * ==========================================
     * HR APPLICATIONS BY JOB
     * ==========================================
     */
    @GetMapping(
            "/hr/applications/job/{jobId}"
    )
    public ResponseEntity<List<Application>>
    getApplicationsByJob(
            @PathVariable Long jobId) {

        return ResponseEntity.ok(
                applicationService
                        .getApplicationsByJob(
                                jobId
                        )
        );
    }

    /*
     * ==========================================
     * ADMIN APPLICATIONS BY JOB
     * ==========================================
     */
    @GetMapping(
            "/admin/applications/job/{jobId}"
    )
    public ResponseEntity<List<Application>>
    getApplicationsByJobAsAdmin(
            @PathVariable Long jobId) {

        return ResponseEntity.ok(
                applicationService
                        .getApplicationsByJob(
                                jobId
                        )
        );
    }

    /*
     * ==========================================
     * HR FILTER BY STATUS
     * ==========================================
     */
    @GetMapping(
            "/hr/applications/status/{status}"
    )
    public ResponseEntity<List<Application>>
    getApplicationsByStatus(
            @PathVariable ApplicationStatus status) {

        return ResponseEntity.ok(
                applicationService
                        .getApplicationsByStatus(
                                status
                        )
        );
    }

    /*
     * ==========================================
     * ADMIN FILTER BY STATUS
     * ==========================================
     */
    @GetMapping(
            "/admin/applications/status/{status}"
    )
    public ResponseEntity<List<Application>>
    getApplicationsByStatusAsAdmin(
            @PathVariable ApplicationStatus status) {

        return ResponseEntity.ok(
                applicationService
                        .getApplicationsByStatus(
                                status
                        )
        );
    }

    /*
     * ==========================================
     * HR UPDATE STATUS
     * ==========================================
     */
    @PutMapping(
            "/hr/applications/{id}/status"
    )
    public ResponseEntity<Application>
    updateStatus(
            @PathVariable Long id,
            @RequestParam ApplicationStatus status,
            Authentication authentication) {

        return ResponseEntity.ok(
                applicationService
                        .updateStatus(
                                id,
                                status,
                                authentication.getName()
                        )
        );
    }

    /*
     * ==========================================
     * ADMIN UPDATE STATUS
     * ==========================================
     */
    @PutMapping(
            "/admin/applications/{id}/status"
    )
    public ResponseEntity<Application>
    updateStatusAsAdmin(
            @PathVariable Long id,
            @RequestParam ApplicationStatus status,
            Authentication authentication) {

        return ResponseEntity.ok(
                applicationService
                        .updateStatus(
                                id,
                                status,
                                authentication.getName()
                        )
        );
    }

    /*
     * ==========================================
     * HR GET STATUS HISTORY
     * ==========================================
     */
    @GetMapping(
            "/hr/applications/{id}/history"
    )
    public ResponseEntity<
            List<ApplicationStatusHistory>>
    getApplicationStatusHistory(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                applicationService
                        .getApplicationStatusHistory(
                                id
                        )
        );
    }

    /*
     * ==========================================
     * ADMIN GET STATUS HISTORY
     * ==========================================
     */
    @GetMapping(
            "/admin/applications/{id}/history"
    )
    public ResponseEntity<
            List<ApplicationStatusHistory>>
    getApplicationStatusHistoryAsAdmin(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                applicationService
                        .getApplicationStatusHistory(
                                id
                        )
        );
    }

    /*
     * ==========================================
     * HR ASSIGN RECRUITER
     * ==========================================
     *
     * POST:
     * /api/hr/applications/1/recruiter?userId=5
     *
     * userId must belong to an HR user.
     */
    @PutMapping(
            "/hr/applications/{id}/recruiter"
    )
    public ResponseEntity<Application>
    assignRecruiter(
            @PathVariable Long id,
            @RequestParam Long userId) {

        return ResponseEntity.ok(
                applicationService
                        .assignRecruiter(
                                id,
                                userId
                        )
        );
    }

    /*
     * ==========================================
     * ADMIN ASSIGN RECRUITER
     * ==========================================
     */
    @PutMapping(
            "/admin/applications/{id}/recruiter"
    )
    public ResponseEntity<Application>
    assignRecruiterAsAdmin(
            @PathVariable Long id,
            @RequestParam Long userId) {

        return ResponseEntity.ok(
                applicationService
                        .assignRecruiter(
                                id,
                                userId
                        )
        );
    }

    /*
     * ==========================================
     * HR ASSIGN INTERVIEWER
     * ==========================================
     *
     * POST:
     * /api/hr/applications/1/interviewer?userId=7
     */
    @PutMapping(
            "/hr/applications/{id}/interviewer"
    )
    public ResponseEntity<Application>
    assignInterviewer(
            @PathVariable Long id,
            @RequestParam Long userId) {

        return ResponseEntity.ok(
                applicationService
                        .assignInterviewer(
                                id,
                                userId
                        )
        );
    }

    /*
     * ==========================================
     * ADMIN ASSIGN INTERVIEWER
     * ==========================================
     */
    @PutMapping(
            "/admin/applications/{id}/interviewer"
    )
    public ResponseEntity<Application>
    assignInterviewerAsAdmin(
            @PathVariable Long id,
            @RequestParam Long userId) {

        return ResponseEntity.ok(
                applicationService
                        .assignInterviewer(
                                id,
                                userId
                        )
        );
    }

    /*
     * ==========================================
     * INTERVIEWER SUBMIT FEEDBACK
     * ==========================================
     *
     * Score:
     * 0 -> 10
     */
    @PostMapping(
            "/interviewer/applications/{id}/feedback"
    )
    public ResponseEntity<InterviewFeedback>
    submitFeedback(
            @PathVariable Long id,
            @RequestParam Integer evaluationScore,
            @RequestParam String feedback,
            Authentication authentication) {

        return ResponseEntity.ok(
                applicationService
                        .submitFeedback(
                                id,
                                authentication.getName(),
                                evaluationScore,
                                feedback
                        )
        );
    }

    /*
     * ==========================================
     * HR VIEW APPLICATION FEEDBACK
     * ==========================================
     */
    @GetMapping(
            "/hr/applications/{id}/feedback"
    )
    public ResponseEntity<List<InterviewFeedback>>
    getApplicationFeedback(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                applicationService
                        .getApplicationFeedback(id)
        );
    }

    /*
     * ==========================================
     * ADMIN VIEW APPLICATION FEEDBACK
     * ==========================================
     */
    @GetMapping(
            "/admin/applications/{id}/feedback"
    )
    public ResponseEntity<List<InterviewFeedback>>
    getApplicationFeedbackAsAdmin(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                applicationService
                        .getApplicationFeedback(id)
        );
    }

    /*
     * ==========================================
     * INTERVIEWER VIEW OWN FEEDBACK
     * ==========================================
     */
    @GetMapping(
            "/interviewer/feedback"
    )
    public ResponseEntity<List<InterviewFeedback>>
    getMyFeedback(
            Authentication authentication) {

        return ResponseEntity.ok(
                applicationService
                        .getInterviewerFeedback(
                                authentication.getName()
                        )
        );
    }
}