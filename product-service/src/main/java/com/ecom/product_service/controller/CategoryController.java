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

import com.ecom.product_service.dto.CategoryRequest;
import com.ecom.product_service.response.CategoryResponse;
import com.ecom.product_service.response.PageResponse;
import com.ecom.product_service.response.SuccessResponse;
import com.ecom.product_service.service.CategoryService;
import com.ecom.product_service.service.MessageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Category", description = "Product category management operations")
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final MessageService messageService;

    @Operation(summary = "Get all categories", description = "Retrieve a paginated list of all product categories with optional search")
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

    @Operation(summary = "Get category by ID", description = "Retrieve detailed information of a specific category")
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

    @Operation(summary = "Create new category", description = "Create a new product category with optional parent category for hierarchical structure")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Category successfully created"),
        @ApiResponse(responseCode = "400", description = "Invalid category data or validation error", 
            content = @Content(schema = @Schema(implementation = com.ecom.product_service.response.ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<SuccessResponse<CategoryResponse>> createCategory(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.createCategory(request);
        String message = messageService.getMessage("success.category.created");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.<CategoryResponse>builder()
                        .message(message)
                        .data(response)
                        .build());
    }

    @Operation(summary = "Update category", description = "Update existing category information including name and parent relationship")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category successfully updated"),
        @ApiResponse(responseCode = "400", description = "Invalid category data or validation error", 
            content = @Content(schema = @Schema(implementation = com.ecom.product_service.response.ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Category not found", 
            content = @Content(schema = @Schema(implementation = com.ecom.product_service.response.ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse<CategoryResponse>> updateCategory(
            @Parameter(description = "Category ID", example = "1") 
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {

        CategoryResponse response = categoryService.updateCategory(id, request);
        String message = messageService.getMessage("success.category.updated");
        return ResponseEntity.ok(SuccessResponse.<CategoryResponse>builder()
                .message(message)
                .data(response)
                .build());
    }

    @Operation(summary = "Delete category", description = "Soft delete (update deleted status) a category from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Category not found", 
            content = @Content(schema = @Schema(implementation = com.ecom.product_service.response.ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteCategory(
            @Parameter(description = "Category ID", example = "1") 
            @PathVariable Long id) {
        categoryService.deleteCategory(id);
        String message = messageService.getMessage("success.category.deleted");
        return ResponseEntity.ok(Map.of("message", message));
    }
}
