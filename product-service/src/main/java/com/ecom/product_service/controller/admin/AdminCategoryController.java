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

import com.ecom.product_service.dto.CategoryRequest;
import com.ecom.product_service.response.CategoryResponse;
import com.ecom.product_service.response.SuccessResponse;
import com.ecom.product_service.service.CategoryService;
import com.ecom.product_service.service.MessageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Admin - Category Management", description = "Admin operations for managing categories")
@SecurityRequirement(name = "bearer-jwt")
@RestController
@RequestMapping("/api/v1/admin/categories")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCategoryController {

    private final CategoryService categoryService;
    private final MessageService messageService;

    @Operation(
        summary = "[ADMIN] Create new category",
        description = "Create a new category with optional parent category. Requires ADMIN role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Category successfully created"),
        @ApiResponse(responseCode = "400", description = "Invalid category data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role")
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

    @Operation(
        summary = "[ADMIN] Update category",
        description = "Update an existing category. Requires ADMIN role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category successfully updated"),
        @ApiResponse(responseCode = "400", description = "Invalid category data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role"),
        @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse<CategoryResponse>> updateCategory(
            @Parameter(description = "Category ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {

        CategoryResponse response = categoryService.updateCategory(id, request);
        String message = messageService.getMessage("success.category.updated");
        return ResponseEntity.ok(SuccessResponse.<CategoryResponse>builder()
                .message(message)
                .data(response)
                .build());
    }

    @Operation(
        summary = "[ADMIN] Delete category",
        description = "Soft delete a category. Requires ADMIN role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category successfully deleted"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role"),
        @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteCategory(
            @Parameter(description = "Category ID", required = true)
            @PathVariable Long id) {
        categoryService.deleteCategory(id);
        String message = messageService.getMessage("success.category.deleted");
        return ResponseEntity.ok(Map.of("message", message));
    }
}
