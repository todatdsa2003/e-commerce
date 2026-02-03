package com.ecom.user_service.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.user_service.config.CookieProperties;
import com.ecom.user_service.dto.request.LoginRequest;
import com.ecom.user_service.dto.request.RegisterRequest;
import com.ecom.user_service.dto.response.AuthResponse;
import com.ecom.user_service.dto.response.UserResponse;
import com.ecom.user_service.security.UserPrincipal;
import com.ecom.user_service.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

        private static final String ACCESS_TOKEN_COOKIE_NAME = "accessToken";
        private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

        private final AuthService authService;
        private final CookieProperties cookieProperties;

        @PostMapping("/register")
        public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
                log.info("Register request for: {}", request.getEmail());
                UserResponse response = authService.register(request);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @PostMapping("/login")
        public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequest request) {
                log.info("Login request for: {}", request.getEmail());
                AuthResponse authResponse = authService.login(request);

                ResponseCookie accessCookie = buildCookie(ACCESS_TOKEN_COOKIE_NAME, authResponse.getToken(),
                                cookieProperties.getAccessTokenMaxAge());
                ResponseCookie refreshCookie = buildCookie(REFRESH_TOKEN_COOKIE_NAME, authResponse.getRefreshToken(),
                                cookieProperties.getRefreshTokenMaxAge());

                log.info("Login successful - tokens set in HttpOnly cookies");
                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                                .body(createSuccessResponse("Login successful"));
        }

        @PostMapping("/refresh")
        public ResponseEntity<Map<String, String>> refreshToken(
                        @CookieValue(name = REFRESH_TOKEN_COOKIE_NAME, required = true) String refreshToken) {
                log.info("Refresh token request");
                AuthResponse authResponse = authService.refreshToken(refreshToken);

                ResponseCookie accessCookie = buildCookie(ACCESS_TOKEN_COOKIE_NAME, authResponse.getToken(),
                                cookieProperties.getAccessTokenMaxAge());
                ResponseCookie refreshCookie = buildCookie(REFRESH_TOKEN_COOKIE_NAME, authResponse.getRefreshToken(),
                                cookieProperties.getRefreshTokenMaxAge());

                log.info("Token refreshed - new tokens set in HttpOnly cookies");
                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                                .body(createSuccessResponse("Token refreshed successfully"));
        }

        @PostMapping("/logout")
        public ResponseEntity<Map<String, String>> logout(Authentication authentication) {
                log.info("Logout request");

                UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
                authService.logout(userPrincipal.getUser());

                ResponseCookie deleteAccessCookie = buildCookie(ACCESS_TOKEN_COOKIE_NAME, "", 0);
                ResponseCookie deleteRefreshCookie = buildCookie(REFRESH_TOKEN_COOKIE_NAME, "", 0);

                log.info("Logout successful - cookies cleared");
                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, deleteAccessCookie.toString())
                                .header(HttpHeaders.SET_COOKIE, deleteRefreshCookie.toString())
                                .body(createSuccessResponse("Logout successful"));
        }

        // Build cookie with config from application.yml
        private ResponseCookie buildCookie(String name, String value, long maxAge) {
                return ResponseCookie.from(name, value)
                                .httpOnly(cookieProperties.isHttpOnly())
                                .secure(cookieProperties.isSecure())
                                .path(cookieProperties.getPath())
                                .maxAge(maxAge)
                                .sameSite(cookieProperties.getSameSite())
                                .build();
        }

        // Create standard success response
        private Map<String, String> createSuccessResponse(String message) {
                Map<String, String> response = new HashMap<>();
                response.put("message", message);
                response.put("status", "success");
                return response;
        }
}
