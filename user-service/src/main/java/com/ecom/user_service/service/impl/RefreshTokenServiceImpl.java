package com.ecom.user_service.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecom.user_service.exception.UnauthorizedException;
import com.ecom.user_service.model.RefreshToken;
import com.ecom.user_service.model.User;
import com.ecom.user_service.repository.RefreshTokenRepository;
import com.ecom.user_service.service.RefreshTokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${refresh-token.expiration}")
    private Long refreshTokenExpiration;

    @Override
    public RefreshToken generateRefreshToken(User user) {
        log.info("Generating refresh token for user: {}", user.getEmail());

        refreshTokenRepository.deleteByUser(user);

        LocalDateTime expiryDate = LocalDateTime.now()
                .plusSeconds(refreshTokenExpiration / 1000);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiryDate(expiryDate)
                .isRevoked(false)
                .build();

        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);
        log.info("Refresh token generated successfully for user: {}", user.getEmail());

        return savedToken;
    }

    @Override
    @Transactional(readOnly = true)
    public RefreshToken validateRefreshToken(String token) {
        log.debug("Validating refresh token: {}", token);

        // Find token
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        // Check if revoked
        if (refreshToken.getIsRevoked()) {
            log.warn("Attempt to use revoked refresh token for user: {}", refreshToken.getUser().getEmail());
            throw new UnauthorizedException("Refresh token has been revoked");
        }

        // Check if expired
        if (refreshToken.isExpired()) {
            log.warn("Attempt to use expired refresh token for user: {}", refreshToken.getUser().getEmail());
            throw new UnauthorizedException("Refresh token has expired");
        }

        log.debug("Refresh token validated successfully");
        return refreshToken;
    }

    @Override
    public void revokeUserTokens(User user) {
        log.info("Revoking refresh tokens for user: {}", user.getEmail());

        List<RefreshToken> tokens = refreshTokenRepository.findByUser(user);
        
        if (tokens.isEmpty()) {
            log.warn("No refresh tokens found for user: {}", user.getEmail());
            return;
        }
        
        tokens.forEach(token -> {
            token.setIsRevoked(true);
            refreshTokenRepository.save(token);
        });
        
        log.info("Revoked {} refresh token(s) for user: {}", tokens.size(), user.getEmail());
    }

    @Override
    public int deleteExpiredTokens() {
        log.info("Deleting expired refresh tokens");
        int deletedCount = refreshTokenRepository.deleteExpiredTokens(LocalDateTime.now());
        log.info("Deleted {} expired refresh tokens", deletedCount);
        return deletedCount;
    }
}
