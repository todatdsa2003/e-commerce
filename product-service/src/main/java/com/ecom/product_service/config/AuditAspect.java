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
        String username = "anonymous";
        String userId = "N/A";
        
        try {
            username = UserContext.getCurrentUsername();
            userId = String.valueOf(UserContext.getCurrentUserId());
        } catch (Exception e) {
            // In case of any exception, retain default values
            
        }

        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String simpleClassName = className.substring(className.lastIndexOf('.') + 1);
        
        // Log with structured format for easy parsing and analysis
        auditLogger.info("USER_ACTION | User: {} (ID: {}) | Action: {} | Controller: {} | Status: SUCCESS",
            username,
            userId,
            methodName,
            simpleClassName
        );
    }
}
