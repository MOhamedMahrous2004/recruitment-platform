package com.recruitment.recruitmentplatform.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI recruitmentPlatformOpenAPI() {

        return new OpenAPI()
                .info(
                        new Info()
                                .title("Recruitment Management Platform API")
                                .version("1.0.0")
                                .description("REST API for the Recruitment Management Platform. "
                                        + "Supports authentication, candidate management, "
                                        + "job applications, application tracking, "
                                        + "recruiter/interviewer assignment, and interview evaluation.")
                                .contact(new Contact().name("Recruitment Platform Team"))
                                .license(new License().name("Academic Project"))
                )
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        "bearerAuth",
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                                .description("Enter JWT token only. Example: eyJhbGciOi...")
                                )
                )
                //  Endpoints
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}