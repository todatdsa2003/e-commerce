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

import com.ecom.product_service.dto.BrandRequest;
import com.ecom.product_service.response.BrandResponse;
import com.ecom.product_service.response.PageResponse;
import com.ecom.product_service.response.SuccessResponse;
import com.ecom.product_service.service.BrandService;
import com.ecom.product_service.service.MessageService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;
    private final MessageService messageService;

    @GetMapping
    public ResponseEntity<PageResponse<BrandResponse>> getAllBrands(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {

        PageResponse<BrandResponse> response = brandService.getAllBrands(page, size, search);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrandResponse> getBrandById(@PathVariable Long id) {
        BrandResponse response = brandService.getBrandById(id);
        return ResponseEntity.ok(response);
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteBrand(@PathVariable Long id) {
        brandService.deleteBrand(id);
        String message = messageService.getMessage("success.brand.deleted");
        return ResponseEntity.ok(Map.of("message", message));
    }
}
