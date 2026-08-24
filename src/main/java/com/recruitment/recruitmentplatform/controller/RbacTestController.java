package com.recruitment.recruitmentplatform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "bearerAuth") // ★★★ فرض التوكن على كل الـ Endpoints في هذا الـ Controller ★★★
public class RbacTestController {

    @Operation(
            summary = "ADMIN Test Endpoint",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "ADMIN access granted",
                            content = @Content(
                                    mediaType = "text/plain",
                                    schema = @Schema(type = "string", example = "ADMIN access granted")
                            )
                    )
            }
    )
    @GetMapping("/admin/test")
    public String adminTest() {
        return "ADMIN access granted";
    }

    @Operation(
            summary = "HR Test Endpoint",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "HR access granted",
                            content = @Content(
                                    mediaType = "text/plain",
                                    schema = @Schema(type = "string", example = "HR access granted")
                            )
                    )
            }
    )
    @GetMapping("/hr/test")
    public String hrTest() {
        return "HR access granted";
    }

    @Operation(
            summary = "INTERVIEWER Test Endpoint",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "INTERVIEWER access granted",
                            content = @Content(
                                    mediaType = "text/plain",
                                    schema = @Schema(type = "string", example = "INTERVIEWER access granted")
                            )
                    )
            }
    )
    @GetMapping("/interviewer/test")
    public String interviewerTest() {
        return "INTERVIEWER access granted";
    }

    @Operation(
            summary = "Authenticated User Test Endpoint",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Authenticated user access granted",
                            content = @Content(
                                    mediaType = "text/plain",
                                    schema = @Schema(type = "string", example = "Authenticated user access granted")
                            )
                    )
            }
    )
    @GetMapping("/user/test")
    public String userTest() {
        return "Authenticated user access granted";
    }
}