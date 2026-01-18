package com.ecom.product_service.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.product_service.dto.ProductRequest;
import com.ecom.product_service.response.PageResponse;
import com.ecom.product_service.response.ProductResponse;
import com.ecom.product_service.response.SuccessResponse;
import com.ecom.product_service.service.MessageService;
import com.ecom.product_service.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Product", description = "Operations related to product management")
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final MessageService messageService;

    @Operation(
        summary = "Get all products",
        description = "Retrieve a paginated list of products with optional filtering by search keyword, status, category, or brand."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved products"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    @GetMapping
    public ResponseEntity<PageResponse<ProductResponse>> getAllProducts(
            @Parameter(description = "Page number (0-indexed)", example = "0") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10") 
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Search keyword to filter products by name or description", example = "iPhone") 
            @RequestParam(required = false) String search,
            @Parameter(description = "Filter by product status ID", example = "1") 
            @RequestParam(required = false) Long statusId,
            @Parameter(description = "Filter by category ID", example = "3") 
            @RequestParam(required = false) Long categoryId,
            @Parameter(description = "Filter by brand ID", example = "2") 
            @RequestParam(required = false) Long brandId) {

        PageResponse<ProductResponse> response = productService.getAllProducts(page, size, search, statusId, categoryId, brandId);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get product by ID",
        description = "Retrieve detailed information about a specific product including its attributes and associated data."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved product"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(
            @Parameter(description = "Unique identifier of the product", example = "1", required = true) 
            @PathVariable Long id) {
        ProductResponse response = productService.getProductById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Create new product",
        description = "Create a new product with complete details including price, availability, category, brand, status, and custom attributes."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Product successfully created"),
        @ApiResponse(responseCode = "400", description = "Invalid product data or validation error")
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
        summary = "Update product",
        description = "Update an existing product's information. All fields in the request will replace the existing values."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product successfully updated"),
        @ApiResponse(responseCode = "400", description = "Invalid product data or validation error"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse<ProductResponse>> updateProduct(
            @Parameter(description = "Unique identifier of the product to update", example = "1", required = true) 
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
        summary = "Delete product",
        description = "Soft delete (update deleted status) a product from the system. This operation cannot be undone."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteProduct(
            @Parameter(description = "Unique identifier of the product to delete", example = "1", required = true) 
            @PathVariable Long id) {
        productService.deleteProduct(id);
        String message = messageService.getMessage("success.product.deleted");
        return ResponseEntity.ok(Map.of("message", message));
    }
}
