package com.ecom.user_service.service;

import com.ecom.user_service.dto.response.UserResponse;

public interface UserService {
    
    // Get user by email
    UserResponse getUserByEmail(String email);
}
