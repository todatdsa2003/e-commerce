package com.ecom.product_service.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.product_service.dto.BulkVariantRequest;
import com.ecom.product_service.dto.ProductVariantOptionRequest;
import com.ecom.product_service.dto.ProductVariantRequest;
import com.ecom.product_service.response.ProductVariantOptionResponse;
import com.ecom.product_service.response.ProductVariantResponse;
import com.ecom.product_service.response.ProductWithVariantsResponse;
import com.ecom.product_service.response.SuccessResponse;
import com.ecom.product_service.service.MessageService;
import com.ecom.product_service.service.ProductVariantService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProductVariantController {

    private final ProductVariantService variantService;
    private final MessageService messageService;

    // Create or update variant options (Admin only)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/products/{productId}/variants/options")
    public ResponseEntity<SuccessResponse<List<ProductVariantOptionResponse>>> createVariantOptions(
            @PathVariable Long productId,
            @Valid @RequestBody List<@Valid ProductVariantOptionRequest> requests) {
        List<ProductVariantOptionResponse> responses = variantService.createOrUpdateVariantOptions(productId, requests);
        String message = messageService.getMessage("success.variant-option.created");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.<List<ProductVariantOptionResponse>>builder()
                        .message(message)
                        .data(responses)
                        .build());
    }

    // Get all variant options for a product
    @GetMapping("/products/{productId}/variants/options")
    public ResponseEntity<List<ProductVariantOptionResponse>> getVariantOptions(@PathVariable Long productId) {
        List<ProductVariantOptionResponse> responses = variantService.getVariantOptions(productId);
        return ResponseEntity.ok(responses);
    }

    // Delete all variant options (Admin only)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/products/{productId}/variants/options")
    public ResponseEntity<Void> deleteVariantOptions(@PathVariable Long productId) {
        variantService.deleteVariantOptions(productId);
        return ResponseEntity.noContent().build();
    }

    // Create single variant (Admin only)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/products/{productId}/variants")
    public ResponseEntity<SuccessResponse<ProductVariantResponse>> createVariant(
            @PathVariable Long productId,
            @Valid @RequestBody ProductVariantRequest request) {
        ProductVariantResponse response = variantService.createVariant(productId, request);
        String message = messageService.getMessage("success.variant.created");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.<ProductVariantResponse>builder()
                        .message(message)
                        .data(response)
                        .build());
    }

    // Create multiple variants in bulk (Admin only)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/products/{productId}/variants/bulk")
    public ResponseEntity<SuccessResponse<List<ProductVariantResponse>>> createVariantsBulk(
            @PathVariable Long productId,
            @Valid @RequestBody BulkVariantRequest request) {
        List<ProductVariantResponse> responses = variantService.createVariantsBulk(productId, request);
        String message = messageService.getMessage("success.variants.created");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.<List<ProductVariantResponse>>builder()
                        .message(message)
                        .data(responses)
                        .build());
    }

    // Get all variants for a product
    @GetMapping("/products/{productId}/variants")
    public ResponseEntity<List<ProductVariantResponse>> getVariants(
            Authentication authentication,
            @PathVariable Long productId,
            @RequestParam(defaultValue = "false") Boolean activeOnly) {
        boolean isAdmin = hasAdminRole(authentication);
        List<ProductVariantResponse> responses = variantService.getVariants(productId, activeOnly, isAdmin);
        return ResponseEntity.ok(responses);
    }

    // Get variant by ID
    @GetMapping("/variants/{variantId}")
    public ResponseEntity<ProductVariantResponse> getVariantById(@PathVariable Long variantId) {
        ProductVariantResponse response = variantService.getVariantById(variantId);
        return ResponseEntity.ok(response);
    }

    // Get default variant for a product
    @GetMapping("/products/{productId}/variants/default")
    public ResponseEntity<ProductVariantResponse> getDefaultVariant(@PathVariable Long productId) {
        ProductVariantResponse response = variantService.getDefaultVariant(productId);
        return ResponseEntity.ok(response);
    }

    // Update variant (Admin only)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/variants/{variantId}")
    public ResponseEntity<SuccessResponse<ProductVariantResponse>> updateVariant(
            @PathVariable Long variantId,
            @Valid @RequestBody ProductVariantRequest request) {
        ProductVariantResponse response = variantService.updateVariant(variantId, request);
        String message = messageService.getMessage("success.variant.updated");
        return ResponseEntity.ok(SuccessResponse.<ProductVariantResponse>builder()
                .message(message)
                .data(response)
                .build());
    }

    // Update variant stock only (Admin only)
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/variants/{variantId}/stock")
    public ResponseEntity<SuccessResponse<ProductVariantResponse>> updateStock(
            @PathVariable Long variantId,
            @RequestBody Map<String, Integer> body) {
        Integer newStock = body.get("stockQuantity");
        ProductVariantResponse response = variantService.updateStock(variantId, newStock);
        String message = messageService.getMessage("success.variant.updated");
        return ResponseEntity.ok(SuccessResponse.<ProductVariantResponse>builder()
                .message(message)
                .data(response)
                .build());
    }

    // Soft delete variant (Admin only)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/variants/{variantId}")
    public ResponseEntity<Void> deleteVariant(@PathVariable Long variantId) {
        variantService.deleteVariant(variantId);
        return ResponseEntity.noContent().build();
    }

    // Get product with all variants
    @GetMapping("/products/{productId}/variants/detail")
    public ResponseEntity<ProductWithVariantsResponse> getProductWithVariants(
            Authentication authentication,
            @PathVariable Long productId) {
        boolean isAdmin = hasAdminRole(authentication);
        ProductWithVariantsResponse response = variantService.getProductWithVariants(productId, isAdmin);
        return ResponseEntity.ok(response);
    }

    private boolean hasAdminRole(Authentication authentication) {
        if (authentication == null) return false;
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
