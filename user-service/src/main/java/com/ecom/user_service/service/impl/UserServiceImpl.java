package com.ecom.user_service.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecom.user_service.dto.request.ChangePasswordRequest;
import com.ecom.user_service.dto.request.UpdateProfileRequest;
import com.ecom.user_service.dto.response.UserResponse;
import com.ecom.user_service.exception.BadRequestException;
import com.ecom.user_service.exception.NotFoundException;
import com.ecom.user_service.mapper.UserMapper;
import com.ecom.user_service.model.User;
import com.ecom.user_service.repository.UserRepository;
import com.ecom.user_service.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        log.debug("Fetching user by email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", email);
                    return new NotFoundException("User not found with email: " + email);
                });

        log.debug("User found: {}", user.getId());
        return userMapper.toUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateProfile(String email, UpdateProfileRequest request) {
        log.info("Updating profile for user: {}", email);

        // Find user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", email);
                    return new NotFoundException("User not found with email: " + email);
                });
        String newFullName = request.getFullName();
        String newPhone = request.getPhoneNumber();

        // Validate phone number limit if changed
        if (newPhone != null && !newPhone.isEmpty() &&
                !newPhone.equals(user.getPhoneNumber())) {

            long phoneCount = userRepository.countByPhoneNumber(newPhone);
            if (phoneCount >= 2) {
                log.error("Phone number {} already has 2 accounts registered", newPhone);
                throw new BadRequestException("This phone number has reached the maximum limit of 2 accounts");
            }
        }
        user.setFullName(newFullName);
        if (newPhone == null || newPhone.isEmpty()) {
            user.setPhoneNumber(null);
        } else {
            user.setPhoneNumber(newPhone);
        }

        User updatedUser = userRepository.save(user);
        log.info("Profile updated successfully for user: {}", email);

        return userMapper.toUserResponse(updatedUser);
    }

    @Override
    @Transactional
    public void changePassword(String email, ChangePasswordRequest request) {
        log.info("Changing password for user: {}", email);

        // Find user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", email);
                    return new NotFoundException("User not found with email: " + email);
                });

        // Validate current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            log.error("Current password is incorrect for user: {}", email);
            throw new BadRequestException("Current password is incorrect");
        }

        // Validate new password != current password
        if (request.getCurrentPassword().equals(request.getNewPassword())) {
            log.error("New password must be different from current password");
            throw new BadRequestException("New password must be different from current password");
        }

        // Validate new password == confirm password
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            log.error("New password and confirm password do not match");
            throw new BadRequestException("New password and confirm password do not match");
        }

        // Update password
        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(encodedPassword);

        userRepository.save(user);
        log.info("Password changed successfully for user: {}", email);
    }
}
