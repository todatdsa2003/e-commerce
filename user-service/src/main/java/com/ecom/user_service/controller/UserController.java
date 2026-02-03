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
import com.ecom.user_service.mapper.UserMapper;
import com.ecom.user_service.model.User;
import com.ecom.user_service.security.UserPrincipal;
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
    private final UserMapper userMapper;

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        // Get User from SecurityContext - no database query needed
        User user = getUserFromAuthentication(authentication);
        log.info("User requesting profile: {}", user.getEmail());

        UserResponse response = userMapper.toUserResponse(user);
        log.info("Profile retrieved successfully for user: {}", user.getEmail());

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request) {

        User user = getUserFromAuthentication(authentication);
        log.info("User requesting profile update: {}", user.getEmail());

        UserResponse updatedUser = userService.updateProfile(user.getEmail(), request);
        log.info("Profile updated successfully for user: {}", user.getEmail());

        return ResponseEntity.ok(updatedUser);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @PatchMapping("/me/password")
    public ResponseEntity<Void> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request) {

        User user = getUserFromAuthentication(authentication);
        log.info("User requesting password change: {}", user.getEmail());

        userService.changePassword(user.getEmail(), request);
        log.info("Password changed successfully for user: {}", user.getEmail());

        return ResponseEntity.noContent().build();
    }

    private User getUserFromAuthentication(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            log.error("Authentication object is null or invalid");
            throw new UnauthorizedException("Authentication required");
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return userPrincipal.getUser();
    }
}
