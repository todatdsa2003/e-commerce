package com.ecom.user_service.service.impl;

import org.springframework.stereotype.Service;

import com.ecom.user_service.dto.response.UserResponse;
import com.ecom.user_service.exception.NotFoundException;
import com.ecom.user_service.mapper.UserMapper;
import com.ecom.user_service.model.User;
import com.ecom.user_service.repository.UserRepository;
import com.ecom.user_service.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    
    @Override
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));
        
        return userMapper.toUserResponse(user);
    }
}
