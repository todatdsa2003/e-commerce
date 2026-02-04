package com.ecom.product_service.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ecom.product_service.security.UserContext;

@Aspect
@Component
public class AuditAspect {

    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT");

    @AfterReturning("@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PatchMapping)")
    public void logModifyingOperations(JoinPoint joinPoint) {
        Long userId = null;
        String role = null;

        try {
            userId = UserContext.getCurrentUserId();
            role = UserContext.getCurrentUserRole();
        } catch (Exception e) {
            auditLogger.warn("Failed to get user context: {}", e.getMessage());
        }

        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String simpleClassName = className.substring(className.lastIndexOf('.') + 1);

        // Log with clear indicator that userId is used (not username)
        auditLogger.info("USER_ACTION | UserID: {} | Role: {} | Action: {} | Controller: {} | Status: SUCCESS",
                userId != null ? userId : "ANONYMOUS",
                role != null ? role : "N/A",
                methodName,
                simpleClassName);
    }
}