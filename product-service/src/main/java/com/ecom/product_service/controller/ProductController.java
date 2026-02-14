package com.ecom.product_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.product_service.response.PageResponse;
import com.ecom.product_service.response.ProductResponse;
import com.ecom.product_service.service.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    // ============================================
    // MICROSERVICES FIELD - COMMENTED FOR LOCAL TESTING
    // Uncomment this when deploying in microservices environment
    // ============================================
    //private final UserClient userClient;
    private final ProductService productService;

    // ============================================
    // MICROSERVICES TEST ENDPOINT - COMMENTED FOR LOCAL TESTING
    // Uncomment this when deploying in microservices environment
    // ============================================
    /*
    // Test endpoint to verify UserService connectivity
    @GetMapping("/get-user-info")
    @CircuitBreaker(name = "userService", fallbackMethod = "testConnectFallback")
    public ResponseEntity<UserDTO> testConnect() {
        UserDTO user = userClient.getCurrentUser();
        return ResponseEntity.ok(user);
    }

    // Fallback method when UserService is unavailable
    private ResponseEntity<UserDTO> testConnectFallback(Throwable throwable) {
        log.warn("Circuit Breaker activated for userService. Reason: {}", throwable.getMessage());

        UserDTO anonymousUser = UserDTO.builder()
                .id(-1L)
                .fullName("System Maintenance")
                .email("N/A")
                .phoneNumber("N/A")
                .role("ANONYMOUS")
                .isActive(false)
                .createdAt(null)
                .build();

        return ResponseEntity.ok(anonymousUser);
    }
    */

    // Get paginated products with filters
    @GetMapping
    public ResponseEntity<PageResponse<ProductResponse>> getAllProducts(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long statusId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long brandId) {
        boolean isAdmin = hasAdminRole(authentication);
        PageResponse<ProductResponse> response = productService.getAllProducts(
                page, size, search, statusId, categoryId, brandId, isAdmin);
        return ResponseEntity.ok(response);
    }

    // Get product details by ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(
            Authentication authentication,
            @PathVariable Long id) {
        boolean isAdmin = hasAdminRole(authentication);
        ProductResponse response = productService.getProductById(id, isAdmin);
        return ResponseEntity.ok(response);
    }

    private boolean hasAdminRole(Authentication authentication) {
        if (authentication == null) return false;
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
