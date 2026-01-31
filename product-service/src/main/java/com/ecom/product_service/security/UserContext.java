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

    public static String getCurrentUserFullName() {
        UserPrincipal user = getCurrentUser();
        return user != null ? user.getFullName() : null;
    }

    public static String getCurrentUserPhoneNumber() {
        UserPrincipal user = getCurrentUser();
        return user != null ? user.getPhoneNumber() : null;
    }

    public static String getCurrentUserRole() {
        UserPrincipal user = getCurrentUser();
        return user != null ? user.getRole() : null;
    }

    public static Boolean isCurrentUserActive() {
        UserPrincipal user = getCurrentUser();
        return user != null ? user.getIsActive() : null;
    }

    public static boolean hasRole(String role) {
        UserPrincipal user = getCurrentUser();
        if (user == null || user.getRole() == null) {
            return false;
        }
        role = user.getRole();
        return role.equals(role) || role.equals("ROLE_" + role);
    }
}
