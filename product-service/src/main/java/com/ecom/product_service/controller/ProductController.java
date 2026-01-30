package com.ecom.product_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.product_service.client.UserClient;
import com.ecom.product_service.dto.UserDTO;
import com.ecom.product_service.response.PageResponse;
import com.ecom.product_service.response.ProductResponse;
import com.ecom.product_service.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Product", description = "Public operations for viewing products")
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    @Autowired
    private UserClient userClient;
    private final ProductService productService;

    @GetMapping("/test-connection")
    public ResponseEntity<UserDTO> testConnect(@RequestHeader("Authorization") String token) {
        // Product Service nhận token từ Postman, rồi chuyển tiếp (forward) sang User
        // Service
        UserDTO user = userClient.getCurrentUser(token);

        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Get all products", description = "Retrieve a paginated list of products with optional filtering by search keyword, status, category, or brand. Public access.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved products"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    @GetMapping
    public ResponseEntity<PageResponse<ProductResponse>> getAllProducts(
            @Parameter(description = "Page number (0-indexed)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Search keyword to filter products by name or description", example = "iPhone") @RequestParam(required = false) String search,
            @Parameter(description = "Filter by product status ID", example = "1") @RequestParam(required = false) Long statusId,
            @Parameter(description = "Filter by category ID", example = "3") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "Filter by brand ID", example = "2") @RequestParam(required = false) Long brandId) {

        PageResponse<ProductResponse> response = productService.getAllProducts(page, size, search, statusId, categoryId,
                brandId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get product by ID", description = "Retrieve detailed information about a specific product including its attributes and associated data. Public access.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved product"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(
            @Parameter(description = "Unique identifier of the product", example = "1", required = true) @PathVariable Long id) {
        ProductResponse response = productService.getProductById(id);
        return ResponseEntity.ok(response);
    }
}
