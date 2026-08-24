package com.recruitment.recruitmentplatform.service;

import com.recruitment.recruitmentplatform.entity.Candidate;
import com.recruitment.recruitmentplatform.entity.User;
import com.recruitment.recruitmentplatform.repository.CandidateRepository;
import com.recruitment.recruitmentplatform.repository.UserRepository;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CandidateService {

    private static final Path CV_UPLOAD_DIRECTORY =
            Paths.get(
                    "uploads",
                    "cvs"
            ).toAbsolutePath().normalize();

    private final CandidateRepository candidateRepository;
    private final UserRepository userRepository;
    private final CvParsingService cvParsingService;

    public CandidateService(
            CandidateRepository candidateRepository,
            UserRepository userRepository,
            CvParsingService cvParsingService) {

        this.candidateRepository =
                candidateRepository;

        this.userRepository =
                userRepository;

        this.cvParsingService =
                cvParsingService;
    }

    /*
     * ==========================================
     * GET CANDIDATE PROFILE
     * ==========================================
     */
    public Candidate getCandidateForUserEmail(
            String email) {

        User user =
                getUserByEmail(email);

        return candidateRepository
                .findByUserId(user.getId())
                .orElseGet(() ->
                        createCandidateFromUser(user)
                );
    }

    /*
     * ==========================================
     * UPDATE CANDIDATE PROFILE
     * ==========================================
     */
    public Candidate updateMyProfile(
            String email,
            String fullName,
            String phone,
            String location) {

        Candidate candidate =
                getCandidateForUserEmail(email);

        if (StringUtils.hasText(fullName)) {

            candidate.setFullName(
                    fullName.trim()
            );
        }

        if (phone != null) {

            candidate.setPhone(
                    phone.trim()
            );
        }

        if (location != null) {

            candidate.setLocation(
                    location.trim()
            );
        }

        return candidateRepository.save(
                candidate
        );
    }

    /*
     * ==========================================
     * SINGLE CV UPLOAD + PARSING
     * ==========================================
     */
    public Candidate uploadCv(
            String email,
            MultipartFile file) {

        validateCvFile(file);

        Candidate candidate =
                getCandidateForUserEmail(email);

        saveCvFile(
                candidate,
                file
        );

        /*
         * Parse candidate information
         * from uploaded CV.
         */
        applyParsedCvData(
                candidate,
                file
        );

        return candidateRepository.save(
                candidate
        );
    }

    /*
     * ==========================================
     * BULK CV UPLOAD + PARSING
     * ==========================================
     */
    public List<Candidate> bulkUploadCvs(
            List<MultipartFile> files,
            List<Long> candidateIds) {

        if (files == null ||
                files.isEmpty()) {

            throw new IllegalArgumentException(
                    "At least one CV file is required"
            );
        }

        if (candidateIds == null ||
                candidateIds.isEmpty()) {

            throw new IllegalArgumentException(
                    "Candidate IDs are required"
            );
        }

        if (files.size() !=
                candidateIds.size()) {

            throw new IllegalArgumentException(
                    "The number of CV files must match the number of candidate IDs"
            );
        }

        List<Candidate> updatedCandidates =
                new ArrayList<>();

        for (int i = 0;
             i < files.size();
             i++) {

            MultipartFile file =
                    files.get(i);

            Long candidateId =
                    candidateIds.get(i);

            if (candidateId == null) {

                throw new IllegalArgumentException(
                        "Candidate ID cannot be null at position "
                                + i
                );
            }

            Candidate candidate =
                    candidateRepository
                            .findById(candidateId)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Candidate not found with ID: "
                                                    + candidateId
                                    )
                            );

            validateCvFile(file);

            saveCvFile(
                    candidate,
                    file
            );

            /*
             * Parse the CV.
             */
            applyParsedCvData(
                    candidate,
                    file
            );

            updatedCandidates.add(
                    candidateRepository.save(
                            candidate
                    )
            );
        }

        return updatedCandidates;
    }

    /*
     * ==========================================
     * PARSE AND APPLY CV DATA
     * ==========================================
     */
    private void applyParsedCvData(
            Candidate candidate,
            MultipartFile file) {

        try {

            CvParsingService.ParsedCvData parsedData =
                    cvParsingService.parse(file);

            /*
             * Update full name only if
             * parser found a reasonable value.
             */
            if (StringUtils.hasText(
                    parsedData.getFullName()
            )) {

                candidate.setFullName(
                        parsedData.getFullName()
                );
            }

            /*
             * Do NOT replace the candidate email
             * automatically unless the parser found
             * a valid email.
             */
            if (StringUtils.hasText(
                    parsedData.getEmail()
            )) {

                String parsedEmail =
                        parsedData.getEmail();

                /*
                 * Avoid database unique email conflicts.
                 *
                 * If another candidate already uses
                 * the extracted email, we keep the
                 * existing candidate email.
                 */
                boolean emailBelongsToAnotherUser =
                        userRepository
                                .findByEmail(parsedEmail)
                                .map(user ->
                                        !user.getId().equals(
                                                candidate.getUser().getId()
                                        )
                                )
                                .orElse(false);

                if (!emailBelongsToAnotherUser) {

                    candidate.setEmail(
                            parsedEmail
                    );
                }
            }

            /*
             * Update phone if found.
             */
            if (StringUtils.hasText(
                    parsedData.getPhone()
            )) {

                candidate.setPhone(
                        parsedData.getPhone()
                );
            }

            /*
             * Update location if found.
             */
            if (StringUtils.hasText(
                    parsedData.getLocation()
            )) {

                candidate.setLocation(
                        parsedData.getLocation()
                );
            }

            /*
             * Update tags / skills if found.
             */
            if (StringUtils.hasText(
                    parsedData.getTags()
            )) {

                candidate.setTags(
                        mergeTags(
                                candidate.getTags(),
                                parsedData.getTags()
                        )
                );
            }

        } catch (RuntimeException e) {

            /*
             * CV upload should still succeed even if
             * parsing could not extract useful data.
             *
             * This means:
             *
             * CV saved ✅
             * Parsing best-effort ✅
             */
            System.out.println(
                    "CV parsing failed: "
                            + e.getMessage()
            );
        }
    }

    /*
     * ==========================================
     * MERGE TAGS
     * ==========================================
     */
    private String mergeTags(
            String existingTags,
            String parsedTags) {

        if (!StringUtils.hasText(
                existingTags
        )) {

            return parsedTags;
        }

        if (!StringUtils.hasText(
                parsedTags
        )) {

            return existingTags;
        }

        java.util.LinkedHashSet<String>
                mergedTags =
                new java.util.LinkedHashSet<>();

        for (String tag :
                existingTags.split(",")) {

            if (StringUtils.hasText(tag)) {

                mergedTags.add(
                        tag.trim()
                );
            }
        }

        for (String tag :
                parsedTags.split(",")) {

            if (StringUtils.hasText(tag)) {

                mergedTags.add(
                        tag.trim()
                );
            }
        }

        return String.join(
                ",",
                mergedTags
        );
    }

    /*
     * ==========================================
     * SAVE CV FILE
     * ==========================================
     */
    private void saveCvFile(
            Candidate candidate,
            MultipartFile file) {

        try {

            Files.createDirectories(
                    CV_UPLOAD_DIRECTORY
            );

            String originalFileName =
                    StringUtils.cleanPath(
                            file.getOriginalFilename() == null
                                    ? "cv"
                                    : file.getOriginalFilename()
                    );

            String extension =
                    getExtension(
                            originalFileName
                    );

            String generatedFileName =
                    UUID.randomUUID()
                            + extension;

            Path targetPath =
                    CV_UPLOAD_DIRECTORY
                            .resolve(generatedFileName)
                            .normalize();

            if (!targetPath.startsWith(
                    CV_UPLOAD_DIRECTORY
            )) {

                throw new IllegalArgumentException(
                        "Invalid CV file name"
                );
            }

            Files.copy(
                    file.getInputStream(),
                    targetPath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            candidate.setCvFileName(
                    originalFileName
            );

            candidate.setCvFilePath(
                    CV_UPLOAD_DIRECTORY
                            .relativize(
                                    targetPath
                            )
                            .toString()
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to save CV file",
                    e
            );
        }
    }

    /*
     * ==========================================
     * UPDATE TAGS
     * ==========================================
     */
    public Candidate updateTags(
            String email,
            String tags) {

        Candidate candidate =
                getCandidateForUserEmail(email);

        candidate.setTags(
                normalizeTags(tags)
        );

        return candidateRepository.save(
                candidate
        );
    }

    /*
     * ==========================================
     * SEARCH CANDIDATES
     * ==========================================
     */
    public List<Candidate> searchCandidates(
            String search) {

        if (!StringUtils.hasText(
                search
        )) {

            return candidateRepository.findAll();
        }

        String normalizedSearch =
                search.trim();

        return candidateRepository
                .findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrTagsContainingIgnoreCase(
                        normalizedSearch,
                        normalizedSearch,
                        normalizedSearch
                );
    }

    /*
     * ==========================================
     * GET CANDIDATE BY ID
     * ==========================================
     */
    public Candidate getCandidateById(
            Long id) {

        return candidateRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Candidate not found with ID: "
                                        + id
                        )
                );
    }

    /*
     * ==========================================
     * FIND USER BY EMAIL
     * ==========================================
     */
    private User getUserByEmail(
            String email) {

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found with email: "
                                        + email
                        )
                );
    }

    /*
     * ==========================================
     * CREATE CANDIDATE FROM USER
     * ==========================================
     */
    private Candidate createCandidateFromUser(
            User user) {

        Candidate candidate =
                new Candidate(
                        user,
                        user.getName(),
                        user.getEmail(),
                        null,
                        null
                );

        return candidateRepository.save(
                candidate
        );
    }

    /*
     * ==========================================
     * CV VALIDATION
     * ==========================================
     */
    private void validateCvFile(
            MultipartFile file) {

        if (file == null ||
                file.isEmpty()) {

            throw new IllegalArgumentException(
                    "CV file is required"
            );
        }

        String fileName =
                file.getOriginalFilename();

        String extension =
                getExtension(fileName)
                        .toLowerCase();

        String contentType =
                file.getContentType();

        boolean validExtension =
                ".pdf".equals(extension)
                        || ".doc".equals(extension)
                        || ".docx".equals(extension);

        boolean validContentType =
                MediaType.APPLICATION_PDF_VALUE
                        .equals(contentType)

                        || "application/msword"
                        .equals(contentType)

                        || "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                        .equals(contentType);

        if (!validExtension ||
                !validContentType) {

            throw new IllegalArgumentException(
                    "Only PDF, DOC, and DOCX CV files are allowed"
            );
        }
    }

    /*
     * ==========================================
     * GET FILE EXTENSION
     * ==========================================
     */
    private String getExtension(
            String fileName) {

        if (!StringUtils.hasText(
                fileName
        )) {

            return "";
        }

        int dotIndex =
                fileName.lastIndexOf('.');

        if (dotIndex < 0) {

            return "";
        }

        return fileName.substring(
                dotIndex
        );
    }

    /*
     * ==========================================
     * NORMALIZE TAGS
     * ==========================================
     */
    private String normalizeTags(
            String tags) {

        if (!StringUtils.hasText(
                tags
        )) {

            return null;
        }

        return tags.trim()
                .replaceAll(
                        "\\s*,\\s*",
                        ","
                )
                .replaceAll(
                        ",+",
                        ","
                );
    }
}