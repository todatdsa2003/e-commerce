package com.ecom.user_service.service;

import com.ecom.user_service.dto.request.LoginRequest;
import com.ecom.user_service.dto.request.RegisterRequest;
import com.ecom.user_service.dto.response.AuthResponse;
import com.ecom.user_service.dto.response.UserResponse;

public interface AuthService {

    UserResponse register(RegisterRequest request);
    
    AuthResponse login(LoginRequest request);
    
    AuthResponse refreshToken(String refreshToken);
    
    void logout(String email);
    
    String processOAuth2Login(String email, String name, String facebookId);
}
