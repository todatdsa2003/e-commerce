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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/categories")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCategoryController {

    private final CategoryService categoryService;
    private final MessageService messageService;

    // Create new category (Admin only)
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

    // Update existing category (Admin only)
    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse<CategoryResponse>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.updateCategory(id, request);
        String message = messageService.getMessage("success.category.updated");
        return ResponseEntity.ok(SuccessResponse.<CategoryResponse>builder()
                .message(message)
                .data(response)
                .build());
    }

    // Soft delete category (Admin only)
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        String message = messageService.getMessage("success.category.deleted");
        return ResponseEntity.ok(Map.of("message", message));
    }
}
