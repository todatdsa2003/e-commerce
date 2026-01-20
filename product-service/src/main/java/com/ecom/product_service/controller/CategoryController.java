package com.ecom.product_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.product_service.response.CategoryResponse;
import com.ecom.product_service.response.PageResponse;
import com.ecom.product_service.service.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Category", description = "Public operations for viewing categories")
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Get all categories", description = "Retrieve a paginated list of all product categories with optional search. Public access.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved categories"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters", 
            content = @Content(schema = @Schema(implementation = com.ecom.product_service.response.ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<PageResponse<CategoryResponse>> getAllCategories(
            @Parameter(description = "Page number (0-indexed)", example = "0") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10") 
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Search keyword to filter categories by name", example = "Electronics") 
            @RequestParam(required = false) String search) {

        PageResponse<CategoryResponse> response = categoryService.getAllCategories(page, size, search);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get category by ID", description = "Retrieve detailed information of a specific category. Public access.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved category"),
        @ApiResponse(responseCode = "404", description = "Category not found", 
            content = @Content(schema = @Schema(implementation = com.ecom.product_service.response.ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(
            @Parameter(description = "Category ID", example = "1") 
            @PathVariable Long id) {
        CategoryResponse response = categoryService.getCategoryById(id);
        return ResponseEntity.ok(response);
    }
}
