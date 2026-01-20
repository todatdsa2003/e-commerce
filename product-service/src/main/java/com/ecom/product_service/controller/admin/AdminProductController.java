package com.ecom.product_service.controller.admin;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.product_service.dto.ProductRequest;
import com.ecom.product_service.response.ProductResponse;
import com.ecom.product_service.response.SuccessResponse;
import com.ecom.product_service.service.MessageService;
import com.ecom.product_service.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@Tag(name = "Admin - Product Management", description = "Admin operations for managing products")
@SecurityRequirement(name = "bearer-jwt")
@RestController
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {

    private final ProductService productService;
    private final MessageService messageService;

    @Operation(
        summary = "[ADMIN] Create new product",
        description = "Create a new product with complete details. Requires ADMIN role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Product successfully created"),
        @ApiResponse(responseCode = "400", description = "Invalid product data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role")
    })
    @PostMapping
    public ResponseEntity<SuccessResponse<ProductResponse>> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        String message = messageService.getMessage("success.product.created");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.<ProductResponse>builder()
                        .message(message)
                        .data(response)
                        .build());
    }

    @Operation(
        summary = "[ADMIN] Update product",
        description = "Update an existing product's information. Requires ADMIN role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product successfully updated"),
        @ApiResponse(responseCode = "400", description = "Invalid product data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse<ProductResponse>> updateProduct(
            @Parameter(description = "Product ID", required = true) 
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {

        ProductResponse response = productService.updateProduct(id, request);
        String message = messageService.getMessage("success.product.updated");
        return ResponseEntity.ok(SuccessResponse.<ProductResponse>builder()
                .message(message)
                .data(response)
                .build());
    }

    @Operation(
        summary = "[ADMIN] Delete product",
        description = "Soft delete a product from the system. Requires ADMIN role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product successfully deleted"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteProduct(
            @Parameter(description = "Product ID", required = true) 
            @PathVariable Long id) {
        productService.deleteProduct(id);
        String message = messageService.getMessage("success.product.deleted");
        return ResponseEntity.ok(Map.of("message", message));
    }
}
