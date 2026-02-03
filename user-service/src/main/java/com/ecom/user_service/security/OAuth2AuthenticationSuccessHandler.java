package com.ecom.user_service.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.ecom.user_service.dto.response.AuthResponse;
import com.ecom.user_service.service.AuthService;

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

    @Value("${app.oauth2.redirect-uri}")
    private String frontendRedirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // Get user info from OAuth2
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String facebookId = oAuth2User.getAttribute("id");

        log.info("Facebook login successful for email: {}", email);

        AuthResponse authResponse = authService.processOAuth2Login(email, name, facebookId);
        ResponseCookie accessTokenCookie = createAccessTokenCookie(authResponse.getToken());
        ResponseCookie refreshTokenCookie = createRefreshTokenCookie(authResponse.getRefreshToken());

        response.addHeader("Set-Cookie", accessTokenCookie.toString());
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());
        log.info("Redirecting to frontend with both tokens in httpOnly cookies: {}", frontendRedirectUri);

        getRedirectStrategy().sendRedirect(request, response, frontendRedirectUri);
    }

    private ResponseCookie createAccessTokenCookie(String accessToken) {
        return ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)             // XSS protection
                .secure(true)               // HTTPS only
                .path("/")
                .maxAge(24 * 60 * 60)       // 24 hours
                .sameSite("Strict")         // CSRF protection
                .build();
    }

    private ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)             // XSS protection
                .secure(true)               // HTTPS only
                .path("/")
                .maxAge(7 * 24 * 60 * 60)   // 7 days
                .sameSite("Strict")         // CSRF protection
                .build();
    }
}
