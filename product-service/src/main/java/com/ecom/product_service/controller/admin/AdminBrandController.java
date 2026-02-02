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

import com.ecom.product_service.dto.BrandRequest;
import com.ecom.product_service.response.BrandResponse;
import com.ecom.product_service.response.SuccessResponse;
import com.ecom.product_service.service.BrandService;
import com.ecom.product_service.service.MessageService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/brands")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminBrandController {

    private final BrandService brandService;
    private final MessageService messageService;

    // Create new brand (Admin only)
    @PostMapping
    public ResponseEntity<SuccessResponse<BrandResponse>> createBrand(@Valid @RequestBody BrandRequest request) {
        BrandResponse response = brandService.createBrand(request);
        String message = messageService.getMessage("success.brand.created");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.<BrandResponse>builder()
                        .message(message)
                        .data(response)
                        .build());
    }

    // Update existing brand (Admin only)
    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse<BrandResponse>> updateBrand(
            @PathVariable Long id,
            @Valid @RequestBody BrandRequest request) {
        BrandResponse response = brandService.updateBrand(id, request);
        String message = messageService.getMessage("success.brand.updated");
        return ResponseEntity.ok(SuccessResponse.<BrandResponse>builder()
                .message(message)
                .data(response)
                .build());
    }

    // Soft delete brand (Admin only)
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteBrand(@PathVariable Long id) {
        brandService.deleteBrand(id);
        String message = messageService.getMessage("success.brand.deleted");
        return ResponseEntity.ok(Map.of("message", message));
    }
}
