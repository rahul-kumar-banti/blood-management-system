package com.bloodbank.config;

import com.bloodbank.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        // Skip JWT processing for authentication endpoints
        String requestURI = request.getRequestURI();
        if (requestURI.contains("/auth/") || requestURI.contains("/public/")) {
            log.debug("Skipping JWT processing for URI: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }
        
        final String authHeader = request.getHeader("Authorization");
        log.debug("Processing request: {} with Authorization header: {}", requestURI, authHeader);
        
        final String jwt;
        final String username;
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("No valid Authorization header found for URI: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }
        
        jwt = authHeader.substring(7);
        username = jwtService.extractUsername(jwt);
        log.debug("Extracted username: {} from JWT for URI: {}", username, requestURI);
        
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            log.debug("Loaded user details for username: {} with authorities: {}", username, userDetails.getAuthorities());
            
            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
                );
                authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.debug("Set authentication context for user: {} with authorities: {}", username, userDetails.getAuthorities());
            } else {
                log.warn("JWT token is not valid for user: {}", username);
            }
        } else if (username == null) {
            log.warn("Could not extract username from JWT token");
        } else {
            log.debug("Authentication context already exists for user: {}", username);
        }
        
        filterChain.doFilter(request, response);
    }
}
