package com.ecom.product_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.product_service.response.ProductStatusResponse;
import com.ecom.product_service.service.ProductStatusService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Product Status", description = "Operations for retrieving product status information")
@RestController
@RequestMapping("/api/v1/product-status")
@RequiredArgsConstructor
public class ProductStatusController {

    private final ProductStatusService productStatusService;

    @Operation(
        summary = "Get all product statuses",
        description = "Retrieve a list of all available product statuses (e.g., active, inactive, discontinued, out of stock)."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved product statuses")
    })
    @GetMapping
    public ResponseEntity<List<ProductStatusResponse>> getAllProductStatuses() {
        List<ProductStatusResponse> response = productStatusService.getAllProductStatuses();
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get product status by ID",
        description = "Retrieve detailed information about a specific product status using its unique identifier."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved product status"),
        @ApiResponse(responseCode = "404", description = "Product status not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductStatusResponse> getProductStatusById(
            @Parameter(description = "Unique identifier of the product status", example = "1", required = true) 
            @PathVariable Long id) {
        ProductStatusResponse response = productStatusService.getProductStatusById(id);
        return ResponseEntity.ok(response);
    }
}
