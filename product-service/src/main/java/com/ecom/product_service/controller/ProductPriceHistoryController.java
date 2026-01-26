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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Product Price History", description = "Operations for tracking and managing product price changes")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Validated
public class ProductPriceHistoryController {

    private final ProductPriceHistoryService productPriceHistoryService;

    @Operation(
        summary = "Get product price history (paginated)",
        description = "Retrieve paginated price change history for a specific product with optional price range filtering. Public access."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved price history"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/products/{productId}/price-history")
    public ResponseEntity<PageResponse<ProductPriceHistoryResponse>> getPriceHistory(
            @Parameter(description = "Unique identifier of the product", example = "1", required = true) 
            @PathVariable Long productId,
            @Parameter(description = "Page number (0-indexed)", example = "0") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10") 
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Minimum price filter", example = "100.00") 
            @RequestParam(required = false) BigDecimal minPrice,
            @Parameter(description = "Maximum price filter", example = "500.00") 
            @RequestParam(required = false) BigDecimal maxPrice) {

        PageResponse<ProductPriceHistoryResponse> response = productPriceHistoryService.getPriceHistoryByProductId(
                productId, page, size, minPrice, maxPrice);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Create price history record",
        description = "Record a price change for a product or variant. Tracks the new price, reason for change, and who made the change. Requires ADMIN or SELLER role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Price history record successfully created"),
        @ApiResponse(responseCode = "400", description = "Invalid request data or validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN or SELLER role"),
        @ApiResponse(responseCode = "404", description = "Product or variant not found")
    })
    @SecurityRequirement(name = "bearer-jwt")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    @PostMapping("/price-history")
    public ResponseEntity<ProductPriceHistoryResponse> createPriceHistory(
            @Valid @RequestBody ProductPriceHistoryRequest request) {

        ProductPriceHistoryResponse response = productPriceHistoryService.createPriceHistory(
                request.getProductId(),
                request.getVariantId(),
                request.getNewPrice(),
                request.getChangeReason(),
                request.getChangedBy()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
        summary = "Get all product price history",
        description = "Retrieve complete price change history for a product, including all variants."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved price history"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/products/{productId}/price-history/all")
    public ResponseEntity<List<ProductPriceHistoryResponse>> getProductPriceHistory(
            @Parameter(description = "Unique identifier of the product", example = "1", required = true) 
            @PathVariable Long productId) {

        List<ProductPriceHistoryResponse> response = productPriceHistoryService.getProductPriceHistory(productId);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get variant price history",
        description = "Retrieve complete price change history for a specific product variant."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved price history"),
        @ApiResponse(responseCode = "404", description = "Variant not found")
    })
    @GetMapping("/variants/{variantId}/price-history")
    public ResponseEntity<List<ProductPriceHistoryResponse>> getVariantPriceHistory(
            @Parameter(description = "Unique identifier of the variant", example = "5", required = true) 
            @PathVariable Long variantId) {

        List<ProductPriceHistoryResponse> response = productPriceHistoryService.getVariantPriceHistory(variantId);
        return ResponseEntity.ok(response);
    }
}
