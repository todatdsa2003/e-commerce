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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Brand", description = "Brand management operations")
@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;
    private final MessageService messageService;

    @Operation(summary = "Get all brands", description = "Retrieve a paginated list of all brands with optional search")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved brands"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters", 
            content = @Content(schema = @Schema(implementation = com.ecom.product_service.response.ErrorResponse.class)))
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

    @Operation(summary = "Get brand by ID", description = "Retrieve detailed information of a specific brand")
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

    @Operation(summary = "Create new brand", description = "Create a new brand with unique name")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Brand successfully created"),
        @ApiResponse(responseCode = "400", description = "Invalid brand data or validation error", 
            content = @Content(schema = @Schema(implementation = com.ecom.product_service.response.ErrorResponse.class)))
    })
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

    @Operation(summary = "Update brand", description = "Update existing brand information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Brand successfully updated"),
        @ApiResponse(responseCode = "400", description = "Invalid brand data or validation error", 
            content = @Content(schema = @Schema(implementation = com.ecom.product_service.response.ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Brand not found", 
            content = @Content(schema = @Schema(implementation = com.ecom.product_service.response.ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse<BrandResponse>> updateBrand(
            @Parameter(description = "Brand ID", example = "1") 
            @PathVariable Long id,
            @Valid @RequestBody BrandRequest request) {

        BrandResponse response = brandService.updateBrand(id, request);
        String message = messageService.getMessage("success.brand.updated");
        return ResponseEntity.ok(SuccessResponse.<BrandResponse>builder()
                .message(message)
                .data(response)
                .build());
    }

    @Operation(summary = "Delete brand", description = "Soft delete (update deleted status) a brand from the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Brand successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Brand not found", 
            content = @Content(schema = @Schema(implementation = com.ecom.product_service.response.ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteBrand(
            @Parameter(description = "Brand ID", example = "1") 
            @PathVariable Long id) {
        brandService.deleteBrand(id);
        String message = messageService.getMessage("success.brand.deleted");
        return ResponseEntity.ok(Map.of("message", message));
    }
}
