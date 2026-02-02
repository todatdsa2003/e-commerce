package com.ecom.product_service.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.product_service.dto.ProductPriceHistoryRequest;
import com.ecom.product_service.response.PageResponse;
import com.ecom.product_service.response.ProductPriceHistoryResponse;
import com.ecom.product_service.service.ProductPriceHistoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Validated
public class ProductPriceHistoryController {

    private final ProductPriceHistoryService productPriceHistoryService;

    // Get paginated price history for a product
    @GetMapping("/products/{productId}/price-history")
    public ResponseEntity<PageResponse<ProductPriceHistoryResponse>> getPriceHistory(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {
        PageResponse<ProductPriceHistoryResponse> response = productPriceHistoryService.getPriceHistoryByProductId(
                productId, page, size, minPrice, maxPrice);
        return ResponseEntity.ok(response);
    }

    // Create price history record (Admin/Seller only)
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    @PostMapping("/products/price-history")
    public ResponseEntity<ProductPriceHistoryResponse> createPriceHistory(
            @Valid @RequestBody ProductPriceHistoryRequest request) {
        ProductPriceHistoryResponse response = productPriceHistoryService.createPriceHistory(
                request.getProductId(),
                request.getVariantId(),
                request.getNewPrice(),
                request.getChangeReason(),
                request.getChangedBy());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Get complete price history for a product
    @GetMapping("/products/{productId}/price-history/all")
    public ResponseEntity<List<ProductPriceHistoryResponse>> getProductPriceHistory(@PathVariable Long productId) {
        List<ProductPriceHistoryResponse> response = productPriceHistoryService.getProductPriceHistory(productId);
        return ResponseEntity.ok(response);
    }

    // Get price history for a specific variant
    @GetMapping("/products/variants/{variantId}/price-history")
    public ResponseEntity<List<ProductPriceHistoryResponse>> getVariantPriceHistory(@PathVariable Long variantId) {
        List<ProductPriceHistoryResponse> response = productPriceHistoryService.getVariantPriceHistory(variantId);
        return ResponseEntity.ok(response);
    }
}
