package com.ecom.user_service.mapper;

import org.springframework.stereotype.Component;

import com.ecom.user_service.dto.request.RegisterRequest;
import com.ecom.user_service.dto.response.UserResponse;
import com.ecom.user_service.model.Role;
import com.ecom.user_service.model.User;

@Component
public class UserMapper {

    public User toEntity(RegisterRequest request, String encodedPassword, Role role) {
        return User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .role(role)
                .isActive(true)
                .build();
    }

    public UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .roleName(user.getRole() != null ? user.getRole().getName() : null)
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
