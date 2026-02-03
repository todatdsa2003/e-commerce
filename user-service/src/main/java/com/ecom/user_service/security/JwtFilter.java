package com.ecom.user_service.security;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ecom.user_service.model.User;
import com.ecom.user_service.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            // Extract JWT token from Cookie FIRST (BFF Pattern), fallback to Authorization header
            String jwt = getJwtFromRequest(request);

            // Validate token and authenticate user
            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {

                //Get userId from token
                Long userId = jwtTokenProvider.getUserIdFromToken(jwt);
                log.debug("JWT token validated for user ID: {}", userId);

                // Load user from database
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> {
                            log.warn("User not found with ID: {}", userId);
                            return new RuntimeException("User not found");
                        });

                // Check if user is active
                if (!user.getIsActive()) {
                    log.warn("User account is inactive: {}", user.getEmail());
                    throw new RuntimeException("User account is inactive");
                }

                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().getName());

                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                        user.getEmail(),                           // Principal (email)
                        null,                                      // Credentials (no password needed)
                        Collections.singletonList(authority)       // Authorities (role)
                    );

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set authentication in SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("User authenticated: {} with role: {}", user.getEmail(), user.getRole().getName());
            }
        } catch (Exception ex) {
            log.error("Could not set user authentication in security context", ex);
        }

        // Continue filter chain
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        // 1. Try to get from Cookie first (BFF Pattern - httpOnly secure)
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    String token = cookie.getValue();
                    if (StringUtils.hasText(token)) {
                        log.debug("JWT token extracted from httpOnly cookie");
                        return token;
                    }
                }
            }
        }

        // 2. Fallback to Authorization header (backward compatibility)
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            log.debug("JWT token extracted from Authorization header");
            return bearerToken.substring(7);
        }

        return null;
    }
}
