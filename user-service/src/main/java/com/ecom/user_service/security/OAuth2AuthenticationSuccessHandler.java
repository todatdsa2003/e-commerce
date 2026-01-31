package com.ecom.user_service.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.ecom.user_service.dto.response.AuthResponse;
import com.ecom.user_service.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthService authService;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        
        // Get user info from OAuth2
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String facebookId = oAuth2User.getAttribute("id");
        
        log.info("Facebook login successful for email: {}", email);
        
        // Process OAuth2 login (create or get user) - now returns AuthResponse with both tokens
        AuthResponse authResponse = authService.processOAuth2Login(email, name, facebookId);
        
        // Return full AuthResponse as JSON (includes access token + refresh token)
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(authResponse));
        
        log.info("Facebook login response sent with access token and refresh token");
    }
}
