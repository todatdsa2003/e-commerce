package com.ecom.product_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.product_service.response.BrandResponse;
import com.ecom.product_service.response.ErrorResponse;
import com.ecom.product_service.response.PageResponse;
import com.ecom.product_service.service.BrandService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Brand", description = "Public operations for viewing brands")
@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @Operation(summary = "Get all brands", description = "Retrieve a paginated list of all brands with optional search. Public access.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved brands"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters", 
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<PageResponse<BrandResponse>> getAllBrands(
            @Parameter(description = "Page number (0-indexed)", example = "0") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10") 
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Search keyword to filter brands by name", example = "Nike") 
            @RequestParam(required = false) String search) {

        PageResponse<BrandResponse> response = brandService.getAllBrands(page, size, search);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get brand by ID", description = "Retrieve detailed information of a specific brand. Public access.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved brand"),
        @ApiResponse(responseCode = "404", description = "Brand not found", 
            content = @Content(schema = @Schema(implementation = com.ecom.product_service.response.ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<BrandResponse> getBrandById(
            @Parameter(description = "Brand ID", example = "1") 
            @PathVariable Long id) {
        BrandResponse response = brandService.getBrandById(id);
        return ResponseEntity.ok(response);
    }
}
