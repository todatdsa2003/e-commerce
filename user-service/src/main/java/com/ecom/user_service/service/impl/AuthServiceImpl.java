package com.ecom.user_service.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecom.user_service.dto.request.LoginRequest;
import com.ecom.user_service.dto.request.RegisterRequest;
import com.ecom.user_service.dto.response.AuthResponse;
import com.ecom.user_service.dto.response.UserResponse;
import com.ecom.user_service.exception.BadRequestException;
import com.ecom.user_service.exception.UnauthorizedException;
import com.ecom.user_service.mapper.UserMapper;
import com.ecom.user_service.model.RefreshToken;
import com.ecom.user_service.model.Role;
import com.ecom.user_service.model.User;
import com.ecom.user_service.repository.RoleRepository;
import com.ecom.user_service.repository.UserRepository;
import com.ecom.user_service.security.JwtTokenProvider;
import com.ecom.user_service.service.AuthService;
import com.ecom.user_service.service.RefreshTokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserMapper userMapper;
    private final RefreshTokenService refreshTokenService;
    
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

        User user = userMapper.toEntity(request, encodedPassword, userRole);

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with id: {}", savedUser.getId());

        return userMapper.toUserResponse(savedUser);
    }

    //Login method
    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
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

        // Generate JWT token (24h)
        String token = jwtTokenProvider.generateToken(user.getEmail());
        log.info("JWT token generated for user: {}", user.getId());
        
        // Generate refresh token (long-lived, 7 days)
        RefreshToken refreshToken = refreshTokenService.generateRefreshToken(user);
        log.info("Refresh token generated for user: {}", user.getId());
        
        UserResponse userResponse = userMapper.toUserResponse(user);
        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken.getToken())
                .type("Bearer")
                .user(userResponse)
                .build();
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(String refreshToken) {
        log.info("Refreshing access token");
    
        RefreshToken validatedToken = refreshTokenService.validateRefreshToken(refreshToken);
        User user = validatedToken.getUser();
        
        String newAccessToken = jwtTokenProvider.generateToken(user.getEmail());
        log.info("New access token generated for user: {}", user.getEmail());
        
        UserResponse userResponse = userMapper.toUserResponse(user);
        
        return AuthResponse.builder()
                .token(newAccessToken)
                .refreshToken(refreshToken)
                .type("Bearer")
                .user(userResponse)
                .build();
    }

    @Override
    @Transactional
    public void logout(String email) {
        log.info("Logging out user: {}", email);
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
        
        refreshTokenService.revokeUserTokens(user);
        log.info("User logged out successfully: {}", email);
    }

    @Override
    @Transactional
    public String processOAuth2Login(String email, String name, String facebookId) {
        log.info("Processing OAuth2 login for email: {}", email);
        
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            log.info("Creating new user from Facebook login: {}", email);
            
            // Get or create default role
            Role userRole = roleRepository.findByName(DEFAULT_ROLE)
                    .orElseGet(() -> {
                        log.info("Creating default role: {}", DEFAULT_ROLE);
                        Role newRole = Role.builder()
                                .name(DEFAULT_ROLE)
                                .description("Default user role")
                                .build();
                        return roleRepository.save(newRole);
                    });
            
            // Create new user from Facebook data
            User newUser = User.builder()
                    .email(email)
                    .fullName(name != null ? name : "Facebook User")
                    .password(passwordEncoder.encode(facebookId)) // Encode Facebook ID as password
                    .role(userRole)
                    .isActive(true)
                    .build();
            
            return userRepository.save(newUser);
        });
        
        // Check if user is active
        if (!user.getIsActive()) {
            log.error("User account is inactive: {}", email);
            throw new UnauthorizedException("User account is inactive");
        }
        
        // Generate JWT token
        String token = jwtTokenProvider.generateToken(user.getEmail());
        log.info("JWT token generated for OAuth2 user: {}", user.getEmail());
        
        return token;
    }
}
