package com.recruitment.recruitmentplatform.controller;

import com.recruitment.recruitmentplatform.entity.Candidate;
import com.recruitment.recruitmentplatform.service.CandidateService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping
public class CandidateController {

    private final CandidateService candidateService;

    public CandidateController(
            CandidateService candidateService) {

        this.candidateService =
                candidateService;
    }

    /*
     * ==========================================
     * GET MY PROFILE
     * ==========================================
     */
    @GetMapping("/api/candidate/profile")
    public ResponseEntity<Candidate> getMyProfile(
            Authentication authentication) {

        Candidate candidate =
                candidateService
                        .getCandidateForUserEmail(
                                authentication.getName()
                        );

        return ResponseEntity.ok(
                candidate
        );
    }

    /*
     * ==========================================
     * UPDATE MY PROFILE
     * ==========================================
     */
    @PutMapping("/api/candidate/profile")
    public ResponseEntity<Candidate> updateMyProfile(
            @RequestParam(required = false)
            String fullName,

            @RequestParam(required = false)
            String phone,

            @RequestParam(required = false)
            String location,

            Authentication authentication) {

        Candidate candidate =
                candidateService.updateMyProfile(
                        authentication.getName(),
                        fullName,
                        phone,
                        location
                );

        return ResponseEntity.ok(
                candidate
        );
    }

    /*
     * ==========================================
     * SINGLE CV UPLOAD
     * ==========================================
     *
     * POST:
     *
     * /api/candidate/profile/cv
     *
     * Content-Type:
     *
     * multipart/form-data
     *
     * file:
     *
     * PDF / DOC / DOCX
     */
    @PostMapping(
            value = "/api/candidate/profile/cv",
            consumes = "multipart/form-data"
    )
    public ResponseEntity<Candidate> uploadCv(
            @RequestPart("file")
            MultipartFile file,

            Authentication authentication) {

        Candidate candidate =
                candidateService.uploadCv(
                        authentication.getName(),
                        file
                );

        return ResponseEntity.ok(
                candidate
        );
    }

    /*
     * ==========================================
     * BULK CV UPLOAD - HR
     * ==========================================
     *
     * Endpoint:
     *
     * POST /api/hr/candidates/bulk-cv
     *
     * Content-Type:
     *
     * multipart/form-data
     *
     * Parameters:
     *
     * files:
     * multiple PDF/DOC/DOCX files
     *
     * candidateIds:
     * candidate IDs in the same order
     *
     * Example:
     *
     * files[0]       -> candidateIds[0]
     * files[1]       -> candidateIds[1]
     * files[2]       -> candidateIds[2]
     */
    @PostMapping(
            value = "/api/hr/candidates/bulk-cv",
            consumes = "multipart/form-data"
    )
    public ResponseEntity<List<Candidate>>
    bulkUploadCvs(
            @RequestPart("files")
            List<MultipartFile> files,

            @RequestParam("candidateIds")
            List<Long> candidateIds) {

        List<Candidate> candidates =
                candidateService.bulkUploadCvs(
                        files,
                        candidateIds
                );

        return ResponseEntity.ok(
                candidates
        );
    }

    /*
     * ==========================================
     * BULK CV UPLOAD - ADMIN
     * ==========================================
     *
     * Endpoint:
     *
     * POST /api/admin/candidates/bulk-cv
     */
    @PostMapping(
            value = "/api/admin/candidates/bulk-cv",
            consumes = "multipart/form-data"
    )
    public ResponseEntity<List<Candidate>>
    bulkUploadCvsAsAdmin(
            @RequestPart("files")
            List<MultipartFile> files,

            @RequestParam("candidateIds")
            List<Long> candidateIds) {

        List<Candidate> candidates =
                candidateService.bulkUploadCvs(
                        files,
                        candidateIds
                );

        return ResponseEntity.ok(
                candidates
        );
    }

    /*
     * ==========================================
     * UPDATE TAGS
     * ==========================================
     */
    @PutMapping(
            "/api/candidate/profile/tags"
    )
    public ResponseEntity<Candidate> updateTags(
            @RequestParam String tags,

            Authentication authentication) {

        Candidate candidate =
                candidateService.updateTags(
                        authentication.getName(),
                        tags
                );

        return ResponseEntity.ok(
                candidate
        );
    }

    /*
     * ==========================================
     * HR SEARCH CANDIDATES
     * ==========================================
     */
    @GetMapping(
            "/api/hr/candidates"
    )
    public ResponseEntity<List<Candidate>>
    searchCandidates(
            @RequestParam(required = false)
            String search) {

        return ResponseEntity.ok(
                candidateService.searchCandidates(
                        search
                )
        );
    }

    /*
     * ==========================================
     * HR GET CANDIDATE BY ID
     * ==========================================
     */
    @GetMapping(
            "/api/hr/candidates/{id}"
    )
    public ResponseEntity<Candidate>
    getCandidateById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                candidateService.getCandidateById(
                        id
                )
        );
    }

    /*
     * ==========================================
     * ADMIN SEARCH CANDIDATES
     * ==========================================
     */
    @GetMapping(
            "/api/admin/candidates"
    )
    public ResponseEntity<List<Candidate>>
    searchCandidatesAsAdmin(
            @RequestParam(required = false)
            String search) {

        return ResponseEntity.ok(
                candidateService.searchCandidates(
                        search
                )
        );
    }

    /*
     * ==========================================
     * ADMIN GET CANDIDATE BY ID
     * ==========================================
     */
    @GetMapping(
            "/api/admin/candidates/{id}"
    )
    public ResponseEntity<Candidate>
    getCandidateByIdAsAdmin(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                candidateService.getCandidateById(
                        id
                )
        );
    }
}