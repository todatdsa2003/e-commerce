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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {

    private final ProductService productService;
    private final MessageService messageService;

    // Create new product (Admin only)
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

    // Update existing product (Admin only)
    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse<ProductResponse>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.updateProduct(id, request);
        String message = messageService.getMessage("success.product.updated");
        return ResponseEntity.ok(SuccessResponse.<ProductResponse>builder()
                .message(message)
                .data(response)
                .build());
    }

    // Soft delete product (Admin only)
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        String message = messageService.getMessage("success.product.deleted");
        return ResponseEntity.ok(Map.of("message", message));
    }
}
