package com.ecom.product_service.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Product Variant", description = "Operations for managing product variants and variant options")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProductVariantController {

    private final ProductVariantService variantService;
    private final MessageService messageService;

    @Operation(
        summary = "Create or update variant options",
        description = "Define variant options and their values for a product. Requires ADMIN role "
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Variant options successfully created"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @SecurityRequirement(name = "bearer-jwt")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/products/{productId}/variants/options")
    public ResponseEntity<SuccessResponse<List<ProductVariantOptionResponse>>> createVariantOptions(
            @Parameter(description = "Unique identifier of the product", example = "1", required = true)
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

    @Operation(
        summary = "Get variant options",
        description = "Retrieve all variant options defined for a product."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved variant options"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/products/{productId}/variants/options")
    public ResponseEntity<List<ProductVariantOptionResponse>> getVariantOptions(
            @Parameter(description = "Unique identifier of the product", example = "1", required = true)
            @PathVariable Long productId) {
        List<ProductVariantOptionResponse> responses = variantService.getVariantOptions(productId);
        return ResponseEntity.ok(responses);
    }

    @Operation(
        summary = "Delete variant options",
        description = "Remove all variant options for a product. Requires ADMIN role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Variant options successfully deleted"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @SecurityRequirement(name = "bearer-jwt")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/products/{productId}/variants/options")
    public ResponseEntity<Void> deleteVariantOptions(
            @Parameter(description = "Unique identifier of the product", example = "1", required = true)
            @PathVariable Long productId) {
        variantService.deleteVariantOptions(productId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Create single variant",
        description = "Create a single product variant with specific option values, price, and stock information. Requires ADMIN role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Variant successfully created"),
        @ApiResponse(responseCode = "400", description = "Invalid variant data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @SecurityRequirement(name = "bearer-jwt")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/products/{productId}/variants")
    public ResponseEntity<SuccessResponse<ProductVariantResponse>> createVariant(
            @Parameter(description = "Unique identifier of the product", example = "1", required = true)
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

    @Operation(
        summary = "Create multiple variants in bulk",
        description = "Create multiple product variants at once along with their variant options. Requires ADMIN role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Variants successfully created"),
        @ApiResponse(responseCode = "400", description = "Invalid variant data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @SecurityRequirement(name = "bearer-jwt")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/products/{productId}/variants/bulk")
    public ResponseEntity<SuccessResponse<List<ProductVariantResponse>>> createVariantsBulk(
            @Parameter(description = "Unique identifier of the product", example = "1", required = true)
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

    @Operation(
        summary = "Get product variants",
        description = "Retrieve all variants for a product with optional filtering for active variants only."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved variants"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/products/{productId}/variants")
    public ResponseEntity<List<ProductVariantResponse>> getVariants(
            @Parameter(description = "Unique identifier of the product", example = "1", required = true)
            @PathVariable Long productId,
            @Parameter(description = "Filter for active variants only", example = "false")
            @RequestParam(defaultValue = "false") Boolean activeOnly) {

        List<ProductVariantResponse> responses = variantService.getVariants(productId, activeOnly);
        return ResponseEntity.ok(responses);
    }

    @Operation(
        summary = "Get variant by ID",
        description = "Retrieve detailed information about a specific product variant."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved variant"),
        @ApiResponse(responseCode = "404", description = "Variant not found")
    })
    @GetMapping("/variants/{variantId}")
    public ResponseEntity<ProductVariantResponse> getVariantById(
            @Parameter(description = "Unique identifier of the variant", example = "5", required = true)
            @PathVariable Long variantId) {
        ProductVariantResponse response = variantService.getVariantById(variantId);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get default variant",
        description = "Retrieve the default variant for a product. Useful for displaying initial product information."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved default variant"),
        @ApiResponse(responseCode = "404", description = "Product or default variant not found")
    })
    @GetMapping("/products/{productId}/variants/default")
    public ResponseEntity<ProductVariantResponse> getDefaultVariant(
            @Parameter(description = "Unique identifier of the product", example = "1", required = true)
            @PathVariable Long productId) {
        ProductVariantResponse response = variantService.getDefaultVariant(productId);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Update variant",
        description = "Update an existing variant's information including price, stock, and other properties. Requires ADMIN role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Variant successfully updated"),
        @ApiResponse(responseCode = "400", description = "Invalid variant data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role"),
        @ApiResponse(responseCode = "404", description = "Variant not found")
    })
    @SecurityRequirement(name = "bearer-jwt")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/variants/{variantId}")
    public ResponseEntity<SuccessResponse<ProductVariantResponse>> updateVariant(
            @Parameter(description = "Unique identifier of the variant", example = "5", required = true)
            @PathVariable Long variantId,
            @Valid @RequestBody ProductVariantRequest request) {

        ProductVariantResponse response = variantService.updateVariant(variantId, request);
        String message = messageService.getMessage("success.variant.updated");

        return ResponseEntity.ok(SuccessResponse.<ProductVariantResponse>builder()
                .message(message)
                .data(response)
                .build());
    }

    @Operation(
        summary = "Update variant stock",
        description = "Update only the stock quantity for a specific variant. Requires ADMIN role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock successfully updated"),
        @ApiResponse(responseCode = "400", description = "Invalid stock quantity"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role"),
        @ApiResponse(responseCode = "404", description = "Variant not found")
    })
    @SecurityRequirement(name = "bearer-jwt")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PatchMapping("/variants/{variantId}/stock")
    public ResponseEntity<SuccessResponse<ProductVariantResponse>> updateStock(
            @Parameter(description = "Unique identifier of the variant", example = "5", required = true)
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

    @Operation(
        summary = "Delete variant",
        description = "Soft delete (update deleted status) a product variant. Requires ADMIN role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Variant successfully deleted"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role"),
        @ApiResponse(responseCode = "404", description = "Variant not found")
    })
    @SecurityRequirement(name = "bearer-jwt")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/variants/{variantId}")
    public ResponseEntity<Void> deleteVariant(
            @Parameter(description = "Unique identifier of the variant", example = "5", required = true)
            @PathVariable Long variantId) {
        variantService.deleteVariant(variantId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Get product with variants",
        description = "Retrieve complete product information including all its variants. Public endpoint for displaying products on storefront."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved product with variants"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/products/{productId}/variants/detail")
    public ResponseEntity<ProductWithVariantsResponse> getProductWithVariants(
            @Parameter(description = "Unique identifier of the product", example = "1", required = true)
            @PathVariable Long productId) {
        ProductWithVariantsResponse response = variantService.getProductWithVariants(productId);
        return ResponseEntity.ok(response);
    }
}
