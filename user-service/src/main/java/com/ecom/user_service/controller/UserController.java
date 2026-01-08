package com.ecom.user_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.user_service.dto.request.ChangePasswordRequest;
import com.ecom.user_service.dto.request.UpdateProfileRequest;
import com.ecom.user_service.dto.response.UserResponse;
import com.ecom.user_service.exception.UnauthorizedException;
import com.ecom.user_service.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    //Get current authenticated user profile
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        String email = getAuthenticatedEmail(authentication);
        log.info("User requesting profile: {}", email);
        
        UserResponse user = userService.getUserByEmail(email);
        log.info("Profile retrieved successfully for user: {}", email);
        
        return ResponseEntity.ok(user);
    }
    
    //Update current user profile 
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request) {
        
        String email = getAuthenticatedEmail(authentication);
        log.info("User requesting profile update: {}", email);
        
        UserResponse updatedUser = userService.updateProfile(email, request);
        log.info("Profile updated successfully for user: {}", email);
        
        return ResponseEntity.ok(updatedUser);
    }
    
    //Change current user password
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @PatchMapping("/me/password")
    public ResponseEntity<Void> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request) {
        
        String email = getAuthenticatedEmail(authentication);
        log.info("User requesting password change: {}", email);
        
        userService.changePassword(email, request);
        log.info("Password changed successfully for user: {}", email);
        
        return ResponseEntity.noContent().build();
    }
    
    //Extract and validate authenticated user email from Spring Security Authentication
    private String getAuthenticatedEmail(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            log.error("Authentication object is null or invalid");
            throw new UnauthorizedException("Authentication required");
        }
        return authentication.getName();
    }
}
