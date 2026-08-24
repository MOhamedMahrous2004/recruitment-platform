package com.recruitment.recruitmentplatform.config;

import com.recruitment.recruitmentplatform.security.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            Map<String, Object> body = new HashMap<>();
            body.put("timestamp", System.currentTimeMillis());
            body.put("status", 403);
            body.put("error", "Forbidden");
            body.put("message", "You don't have permission to access this resource");
            body.put("path", request.getRequestURI());

            PrintWriter writer = response.getWriter();
            writer.write(new ObjectMapper().writeValueAsString(body));
            writer.flush();
        };
    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**")
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth
                        // ✅ Allow login and refresh without authentication
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/refresh").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/hr/**").hasRole("HR")
                        .requestMatchers("/api/candidate/**").hasRole("CANDIDATE")
                        .requestMatchers("/api/interviewer/**").hasRole("INTERVIEWER")
                        .anyRequest().authenticated()
                )

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(401, "Unauthorized")
                        )
                        .accessDeniedHandler(accessDeniedHandler())
                )

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/register", "/login", "/css/**", "/js/**", "/images/**",
                                "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/hr/**").hasRole("HR")
                        .requestMatchers("/candidate/**").hasRole("CANDIDATE")
                        .requestMatchers("/interviewer/**").hasRole("INTERVIEWER")
                        .requestMatchers(HttpMethod.GET, "/jobs", "/jobs/*").authenticated()
                        .requestMatchers(HttpMethod.POST, "/jobs/create").hasAnyRole("ADMIN", "HR")
                        .requestMatchers("/jobs/create", "/jobs/*/edit").hasAnyRole("ADMIN", "HR")
                        .requestMatchers(HttpMethod.POST, "/jobs/*/edit").hasAnyRole("ADMIN", "HR")
                        .requestMatchers(HttpMethod.POST, "/jobs/*/delete").hasAnyRole("ADMIN", "HR")
                        .requestMatchers("/dashboard").authenticated()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }
}