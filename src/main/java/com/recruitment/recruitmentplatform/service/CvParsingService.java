package com.recruitment.recruitmentplatform.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CvParsingService {

    /*
     * ==========================================
     * EMAIL PATTERN
     * ==========================================
     */
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile(
                    "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}",
                    Pattern.CASE_INSENSITIVE
            );

    /*
     * ==========================================
     * PHONE PATTERN
     * ==========================================
     *
     * Supports common international and
     * Egyptian-style phone numbers.
     */
    private static final Pattern PHONE_PATTERN =
            Pattern.compile(
                    "(?:\\+?\\d[\\d\\s().-]{7,}\\d)"
            );

    /*
     * ==========================================
     * SKILL KEYWORDS
     * ==========================================
     *
     * Basic parser:
     * if one of these technologies appears
     * in the CV, it is added to tags.
     */
    private static final String[] KNOWN_SKILLS = {

            "Java",
            "Spring",
            "Spring Boot",
            "Spring Security",
            "Hibernate",
            "JPA",
            "MySQL",
            "PostgreSQL",
            "Oracle",
            "SQL",
            "Python",
            "Django",
            "Flask",
            "JavaScript",
            "TypeScript",
            "React",
            "Angular",
            "Vue",
            "HTML",
            "CSS",
            "Bootstrap",
            "Git",
            "GitHub",
            "Docker",
            "Kubernetes",
            "REST",
            "REST API",
            "Microservices",
            "AWS",
            "Azure",
            "C++",
            "C#",
            ".NET",
            "PHP",
            "Laravel"
    };

    /*
     * ==========================================
     * MAIN PARSER
     * ==========================================
     */
    public ParsedCvData parse(
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

        try {

            String extractedText;

            switch (extension) {

                case ".pdf":

                    extractedText =
                            extractPdfText(file);

                    break;

                case ".docx":

                    extractedText =
                            extractDocxText(file);

                    break;

                case ".doc":

                    extractedText =
                            extractDocText(file);

                    break;

                default:

                    throw new IllegalArgumentException(
                            "Unsupported CV format. Allowed formats: PDF, DOC, DOCX"
                    );
            }

            return extractCandidateData(
                    extractedText
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to parse CV file",
                    e
            );
        }
    }

    /*
     * ==========================================
     * PDF TEXT EXTRACTION
     * ==========================================
     */
    private String extractPdfText(
            MultipartFile file)
            throws IOException {

        byte[] bytes =
                file.getBytes();

        try (PDDocument document =
                     Loader.loadPDF(bytes)) {

            PDFTextStripper stripper =
                    new PDFTextStripper();

            return stripper.getText(
                    document
            );
        }
    }

    /*
     * ==========================================
     * DOCX TEXT EXTRACTION
     * ==========================================
     */
    private String extractDocxText(
            MultipartFile file)
            throws IOException {

        try (InputStream inputStream =
                     file.getInputStream();

             XWPFDocument document =
                     new XWPFDocument(inputStream)) {

            StringBuilder text =
                    new StringBuilder();

            document.getParagraphs()
                    .forEach(paragraph -> {

                        String paragraphText =
                                paragraph.getText();

                        if (StringUtils.hasText(
                                paragraphText
                        )) {

                            text.append(
                                    paragraphText
                            );

                            text.append("\n");
                        }
                    });

            return text.toString();
        }
    }

    /*
     * ==========================================
     * DOC TEXT EXTRACTION
     * ==========================================
     */
    private String extractDocText(
            MultipartFile file)
            throws IOException {

        byte[] bytes =
                file.getBytes();

        try (ByteArrayInputStream inputStream =
                     new ByteArrayInputStream(bytes);

             HWPFDocument document =
                     new HWPFDocument(inputStream);

             WordExtractor extractor =
                     new WordExtractor(document)) {

            return extractor.getText();
        }
    }

    /*
     * ==========================================
     * EXTRACT CANDIDATE INFORMATION
     * ==========================================
     */
    private ParsedCvData extractCandidateData(
            String text) {

        ParsedCvData data =
                new ParsedCvData();

        if (!StringUtils.hasText(text)) {

            return data;
        }

        String normalizedText =
                text.replace(
                        "\r\n",
                        "\n"
                ).replace(
                        "\r",
                        "\n"
                );

        /*
         * Extract email.
         */
        Matcher emailMatcher =
                EMAIL_PATTERN.matcher(
                        normalizedText
                );

        if (emailMatcher.find()) {

            data.setEmail(
                    emailMatcher.group()
                            .trim()
            );
        }

        /*
         * Extract phone.
         */
        Matcher phoneMatcher =
                PHONE_PATTERN.matcher(
                        normalizedText
                );

        if (phoneMatcher.find()) {

            String phone =
                    phoneMatcher.group()
                            .trim();

            phone =
                    phone.replaceAll(
                            "\\s+",
                            " "
                    );

            data.setPhone(phone);
        }

        /*
         * Extract name.
         */
        data.setFullName(
                extractName(
                        normalizedText,
                        data.getEmail()
                )
        );

        /*
         * Extract location.
         */
        data.setLocation(
                extractLocation(
                        normalizedText
                )
        );

        /*
         * Extract skills.
         */
        data.setTags(
                extractSkills(
                        normalizedText
                )
        );

        return data;
    }

    /*
     * ==========================================
     * EXTRACT NAME
     * ==========================================
     *
     * Strategy:
     *
     * Look at the first non-empty lines.
     *
     * Skip:
     * - email
     * - phone
     * - CV/Resume headings
     */
    private String extractName(
            String text,
            String email) {

        String[] lines =
                text.split("\\n");

        int inspectedLines = 0;

        for (String line : lines) {

            if (inspectedLines >= 8) {

                break;
            }

            inspectedLines++;

            String value =
                    line.trim();

            if (!StringUtils.hasText(value)) {

                continue;
            }

            if (value.equalsIgnoreCase(
                    "CV"
            ) ||
                    value.equalsIgnoreCase(
                            "Resume"
                    ) ||
                    value.equalsIgnoreCase(
                            "Curriculum Vitae"
                    )) {

                continue;
            }

            if (email != null &&
                    value.contains(email)) {

                continue;
            }

            if (EMAIL_PATTERN.matcher(
                    value
            ).find()) {

                continue;
            }

            if (PHONE_PATTERN.matcher(
                    value
            ).find()) {

                continue;
            }

            if (value.length() > 80) {

                continue;
            }

            /*
             * Avoid obvious section headings.
             */
            if (isSectionHeading(value)) {

                continue;
            }

            return value;
        }

        return null;
    }

    /*
     * ==========================================
     * EXTRACT LOCATION
     * ==========================================
     */
    private String extractLocation(
            String text) {

        String[] lines =
                text.split("\\n");

        for (int i = 0;
             i < lines.length;
             i++) {

            String currentLine =
                    lines[i].trim();

            if (!StringUtils.hasText(
                    currentLine
            )) {

                continue;
            }

            if (startsWithLabel(
                    currentLine,
                    "Location",
                    "Address",
                    "المدينة",
                    "العنوان",
                    "الموقع"
            )) {

                String value =
                        extractValueAfterLabel(
                                currentLine
                        );

                if (StringUtils.hasText(
                        value
                )) {

                    return value;
                }

                if (i + 1 < lines.length) {

                    String nextLine =
                            lines[i + 1].trim();

                    if (StringUtils.hasText(
                            nextLine
                    )) {

                        return nextLine;
                    }
                }
            }
        }

        return null;
    }

    /*
     * ==========================================
     * EXTRACT SKILLS
     * ==========================================
     */
    private String extractSkills(
            String text) {

        Set<String> skills =
                new LinkedHashSet<>();

        for (String skill :
                KNOWN_SKILLS) {

            Pattern pattern =
                    Pattern.compile(
                            "(?<![A-Za-z0-9])"
                                    + Pattern.quote(skill)
                                    + "(?![A-Za-z0-9])",
                            Pattern.CASE_INSENSITIVE
                    );

            if (pattern.matcher(
                    text
            ).find()) {

                skills.add(skill);
            }
        }

        /*
         * Check explicit Skills section
         * for extra simple comma-separated
         * values.
         */
        extractSkillsSection(
                text,
                skills
        );

        return skills.isEmpty()
                ? null
                : String.join(
                ",",
                skills
        );
    }

    /*
     * ==========================================
     * EXTRACT SKILLS SECTION
     * ==========================================
     */
    private void extractSkillsSection(
            String text,
            Set<String> skills) {

        String[] lines =
                text.split("\\n");

        boolean inSkillsSection = false;

        for (String rawLine : lines) {

            String line =
                    rawLine.trim();

            if (!StringUtils.hasText(line)) {

                continue;
            }

            if (isSkillsHeading(line)) {

                inSkillsSection = true;
                continue;
            }

            if (inSkillsSection &&
                    isSectionHeading(line)) {

                inSkillsSection = false;
                continue;
            }

            if (inSkillsSection) {

                String[] values =
                        line.split(
                                "[,;|•]"
                        );

                for (String value :
                        values) {

                    String skill =
                            value.trim();

                    if (skill.length() >= 2 &&
                            skill.length() <= 40) {

                        skills.add(skill);
                    }
                }
            }
        }
    }

    /*
     * ==========================================
     * SECTION HEADING CHECK
     * ==========================================
     */
    private boolean isSectionHeading(
            String line) {

        String value =
                line.trim()
                        .toLowerCase();

        return value.matches(
                "^(summary|profile|objective|education|experience|work experience|employment|skills|technical skills|projects|certifications|languages|references|contact|about|ملخص|الهدف|التعليم|الخبرة|المهارات|المشروعات|الشهادات|اللغات|المراجع).*[ :]?$"
        );
    }

    /*
     * ==========================================
     * SKILLS HEADING
     * ==========================================
     */
    private boolean isSkillsHeading(
            String line) {

        String value =
                line.trim()
                        .toLowerCase();

        return value.equals("skills")
                || value.equals("technical skills")
                || value.equals("skills & technologies")
                || value.equals("مهارات")
                || value.equals("المهارات");
    }

    /*
     * ==========================================
     * LABEL CHECK
     * ==========================================
     */
    private boolean startsWithLabel(
            String line,
            String... labels) {

        String normalized =
                line.trim()
                        .toLowerCase();

        for (String label : labels) {

            if (normalized.startsWith(
                    label.toLowerCase()
            )) {

                return true;
            }
        }

        return false;
    }

    /*
     * ==========================================
     * VALUE AFTER LABEL
     * ==========================================
     */
    private String extractValueAfterLabel(
            String line) {

        int colonIndex =
                line.indexOf(':');

        if (colonIndex >= 0 &&
                colonIndex + 1 < line.length()) {

            return line.substring(
                    colonIndex + 1
            ).trim();
        }

        return "";
    }

    /*
     * ==========================================
     * FILE EXTENSION
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
     * PARSED CV DATA
     * ==========================================
     */
    public static class ParsedCvData {

        private String fullName;
        private String email;
        private String phone;
        private String location;
        private String tags;

        public ParsedCvData() {
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(
                String fullName) {
            this.fullName = fullName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(
                String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(
                String phone) {
            this.phone = phone;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(
                String location) {
            this.location = location;
        }

        public String getTags() {
            return tags;
        }

        public void setTags(
                String tags) {
            this.tags = tags;
        }
    }
}