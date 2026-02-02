package com.ecom.product_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.product_service.dto.ProductDescriptionRequest;
import com.ecom.product_service.response.ProductDescriptionResponse;
import com.ecom.product_service.service.ProductDescriptionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class ProductDescriptionController {

    private final ProductDescriptionService productDescriptionService;

    // Generate AI product description
    @PostMapping("/generate-description")
    public ResponseEntity<ProductDescriptionResponse> generateDescription(
            @Valid @RequestBody ProductDescriptionRequest request) {
        ProductDescriptionResponse response = productDescriptionService.generateDescription(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
