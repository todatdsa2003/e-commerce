package com.ecom.user_service.service;

import com.ecom.user_service.model.RefreshToken;
import com.ecom.user_service.model.User;

public interface RefreshTokenService {

    RefreshToken generateRefreshToken(User user);
    RefreshToken validateRefreshToken(String token);
    void revokeUserTokens(User user);
    int deleteExpiredTokens();
}
