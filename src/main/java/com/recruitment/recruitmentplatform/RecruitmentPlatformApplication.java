package com.recruitment.recruitmentplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching  // ✅ (Caching)
public class RecruitmentPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecruitmentPlatformApplication.class, args);
    }

}