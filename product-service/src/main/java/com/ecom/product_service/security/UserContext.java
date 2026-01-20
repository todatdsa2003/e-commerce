package com.ecom.product_service.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserContext {

    public static UserPrincipal getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            return (UserPrincipal) authentication.getPrincipal();
        }
        
        return null;
    }

    public static Long getCurrentUserId() {
        UserPrincipal user = getCurrentUser();
        return user != null ? user.getId() : null;
    }

    public static String getCurrentUsername() {
        UserPrincipal user = getCurrentUser();
        return user != null ? user.getUsername() : null;
    }


    public static String getCurrentUserEmail() {
        UserPrincipal user = getCurrentUser();
        return user != null ? user.getEmail() : null;
    }

    public static boolean hasRole(String role) {
        UserPrincipal user = getCurrentUser();
        return user != null && user.getRoles().contains(role);
    }
}
