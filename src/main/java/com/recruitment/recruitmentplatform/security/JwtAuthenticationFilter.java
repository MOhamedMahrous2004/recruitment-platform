package com.recruitment.recruitmentplatform.security;

import com.recruitment.recruitmentplatform.service.CustomUserDetailsService;
import com.recruitment.recruitmentplatform.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            CustomUserDetailsService userDetailsService) {

        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        /*
         * Get Authorization header.
         */
        final String authHeader =
                request.getHeader("Authorization");

        /*
         * If there is no Authorization header,
         * continue normally.
         */
        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        /*
         * Remove "Bearer " from the beginning.
         */
        final String jwt =
                authHeader.substring(7);

        String username;

        try {

            /*
             * Extract email/username from JWT.
             */
            username =
                    jwtService.extractUsername(jwt);

        } catch (Exception e) {

            /*
             * Invalid JWT.
             */
            filterChain.doFilter(request, response);
            return;
        }

        /*
         * Check if the user is not already authenticated.
         */
        if (username != null &&
                SecurityContextHolder
                        .getContext()
                        .getAuthentication() == null) {

            /*
             * Load user from database.
             */
            UserDetails userDetails =
                    userDetailsService
                            .loadUserByUsername(username);

            /*
             * Validate JWT.
             */
            if (jwtService.isTokenValid(
                    jwt,
                    userDetails)) {

                /*
                 * Create authentication object.
                 */
                UsernamePasswordAuthenticationToken
                        authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                /*
                 * Add request details.
                 */
                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                /*
                 * Tell Spring Security that
                 * the user is authenticated.
                 */
                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);
            }
        }

        /*
         * Continue the request.
         */
        filterChain.doFilter(request, response);
    }
}