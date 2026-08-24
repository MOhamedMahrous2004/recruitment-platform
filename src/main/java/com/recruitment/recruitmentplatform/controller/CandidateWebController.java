package com.recruitment.recruitmentplatform.controller;

import com.recruitment.recruitmentplatform.entity.Application;
import com.recruitment.recruitmentplatform.entity.Candidate;
import com.recruitment.recruitmentplatform.service.ApplicationService;
import com.recruitment.recruitmentplatform.service.CandidateService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/candidate")
public class CandidateWebController {

    private final CandidateService candidateService;
    private final ApplicationService applicationService;

    public CandidateWebController(CandidateService candidateService, ApplicationService applicationService) {
        this.candidateService = candidateService;
        this.applicationService = applicationService;
    }

    // Upload CV
    @GetMapping("/profile")
    public String profilePage(Authentication authentication, Model model) {
        Candidate candidate = candidateService.getCandidateForUserEmail(authentication.getName());
        model.addAttribute("candidate", candidate);
        return "candidate-profile";
    }

    // رفع  CV من الويب (Session Authentication مش JWT)
    @PostMapping("/profile/upload")
    public String uploadCv(@RequestParam("cvFile") MultipartFile file,
                           Authentication authentication,
                           RedirectAttributes redirectAttributes) {
        try {
            candidateService.uploadCv(authentication.getName(), file);
            redirectAttributes.addFlashAttribute("success", "✅ تم رفع السيرة الذاتية بنجاح!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ فشل رفع الملف: " + e.getMessage());
        }
        return "redirect:/candidate/profile";
    }

    // (Applications)
    @GetMapping("/applications")
    public String myApplications(Authentication authentication, Model model) {
        List<Application> applications = applicationService.getMyApplications(authentication.getName());
        model.addAttribute("applications", applications);
        return "candidate-applications";
    }
}