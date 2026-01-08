package com.ecom.user_service.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ecom.user_service.service.RefreshTokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenCleanupScheduler {

    private final RefreshTokenService refreshTokenService;

    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupExpiredTokens() {
        log.info("Starting scheduled cleanup of expired refresh tokens");
        int deletedCount = refreshTokenService.deleteExpiredTokens();
        log.info("Scheduled cleanup completed. Deleted {} expired tokens", deletedCount);
    }
}
