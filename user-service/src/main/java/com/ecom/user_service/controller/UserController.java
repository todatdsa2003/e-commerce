package com.ecom.user_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.user_service.dto.response.UserResponse;
import com.ecom.user_service.exception.UnauthorizedException;
import com.ecom.user_service.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            log.error("Authentication object is null or invalid");
            throw new UnauthorizedException("Authentication required");
        }
        
        String email = authentication.getName();
        log.debug("Getting profile for user: {}", email);
        
        UserResponse user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }
}
