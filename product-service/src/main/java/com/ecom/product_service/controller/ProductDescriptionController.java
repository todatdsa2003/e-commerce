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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "AI Product Description", description = "AI-powered product description generation (not working because of missing API key)")
@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class ProductDescriptionController {

    private final ProductDescriptionService productDescriptionService;

    @Operation(
        summary = "Generate AI product description",
        description = "Generate a professional product description using AI based on provided keywords, tone, and additional information. Useful for creating compelling product content."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Description successfully generated"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    @PostMapping("/generate-description")
    public ResponseEntity<ProductDescriptionResponse> generateDescription(
            @Valid @RequestBody ProductDescriptionRequest request) {
        
        ProductDescriptionResponse response = productDescriptionService.generateDescription(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
