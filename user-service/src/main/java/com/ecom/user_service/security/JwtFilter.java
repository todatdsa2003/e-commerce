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
            //Extract JWT token from request header
            String jwt = getJwtFromRequest(request);

            // Validate token and authenticate user
            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
                
                //Get email from token
                String email = jwtTokenProvider.getEmailFromToken(jwt);
                log.debug("JWT token validated for email: {}", email);

                // Load user from database
                User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> {
                            log.warn("User not found: {}", email);
                            return new RuntimeException("User not found");
                        });

                // Check if user is active
                if (!user.getIsActive()) {
                    log.warn("User account is inactive: {}", email);
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
                log.debug("User authenticated: {} with role: {}", email, user.getRole().getName());
            }
        } catch (Exception ex) {
            log.error("Could not set user authentication in security context", ex);
        }

        // Continue filter chain
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
    
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        return null;
    }
}
