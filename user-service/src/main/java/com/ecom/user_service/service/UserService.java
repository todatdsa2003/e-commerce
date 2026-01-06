package com.ecom.user_service.service;

import com.ecom.user_service.dto.request.ChangePasswordRequest;
import com.ecom.user_service.dto.request.UpdateProfileRequest;
import com.ecom.user_service.dto.response.UserResponse;

public interface UserService {
    
    UserResponse getUserByEmail(String email);
    UserResponse updateProfile(String email, UpdateProfileRequest request);
    void changePassword(String email, ChangePasswordRequest request);
}
