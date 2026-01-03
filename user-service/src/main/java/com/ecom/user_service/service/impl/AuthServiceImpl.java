package com.ecom.user_service.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecom.user_service.dto.request.LoginRequest;
import com.ecom.user_service.dto.request.RegisterRequest;
import com.ecom.user_service.dto.response.UserResponse;
import com.ecom.user_service.exception.BadRequestException;
import com.ecom.user_service.exception.UnauthorizedException;
import com.ecom.user_service.mapper.UserMapper;
import com.ecom.user_service.model.Role;
import com.ecom.user_service.model.User;
import com.ecom.user_service.repository.RoleRepository;
import com.ecom.user_service.repository.UserRepository;
import com.ecom.user_service.service.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private static final String DEFAULT_ROLE = "ROLE_USER";

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());

        // Validate email not exists
        if (userRepository.existsByEmail(request.getEmail())) {
            log.error("Email already exists: {}", request.getEmail());
            throw new BadRequestException("Email already exists: " + request.getEmail());
        }

        // Validate password match
        if (!request.getPassword().equals(request.getRetypePassword())) {
            log.error("Passwords do not match for email: {}", request.getEmail());
            throw new BadRequestException("Passwords do not match");
        }
        
        // Validate phone number limit
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
            long phoneCount = userRepository.countByPhoneNumber(request.getPhoneNumber());
            if (phoneCount >= 2) {
                log.error("Phone number {} already has 2 accounts registered", request.getPhoneNumber());
                throw new BadRequestException("This phone number has reached the maximum limit of 2 accounts");
            }
        }

        Role userRole = roleRepository.findByName(DEFAULT_ROLE)
                .orElseGet(() -> {
                    log.info("Creating default role: {}", DEFAULT_ROLE);
                    Role newRole = Role.builder()
                            .name(DEFAULT_ROLE)
                            .description("Default user role")
                            .build();
                    return roleRepository.save(newRole);
                });

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        log.debug("Password encoded successfully");

        User user = UserMapper.toEntity(request, encodedPassword, userRole);

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with id: {}", savedUser.getId());

        return UserMapper.toResponse(savedUser);
    }

    //Login method
    @Override
    @Transactional(readOnly = true)
    public UserResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", request.getEmail());
                    return new UnauthorizedException("Invalid email or password");
                });

        // Validate password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.error("Invalid password for email: {}", request.getEmail());
            throw new UnauthorizedException("Invalid email or password");
        }

        // Check  user is active
        if (!user.getIsActive()) {
            log.error("User account is inactive: {}", request.getEmail());
            throw new UnauthorizedException("User account is inactive");
        }

        log.info("User logged in successfully: {}", user.getId());
        return UserMapper.toResponse(user);
    }
}
