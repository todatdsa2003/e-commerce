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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@Tag(name = "Admin - Brand Management", description = "Admin operations for managing brands")
@SecurityRequirement(name = "bearer-jwt")
@RestController
@RequestMapping("/api/v1/admin/brands")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminBrandController {

    private final BrandService brandService;
    private final MessageService messageService;

    @Operation(
        summary = "[ADMIN] Create new brand",
        description = "Create a new brand. Requires ADMIN role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Brand successfully created"),
        @ApiResponse(responseCode = "400", description = "Invalid brand data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role")
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

    @Operation(
        summary = "[ADMIN] Update brand",
        description = "Update an existing brand. Requires ADMIN role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Brand successfully updated"),
        @ApiResponse(responseCode = "400", description = "Invalid brand data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role"),
        @ApiResponse(responseCode = "404", description = "Brand not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse<BrandResponse>> updateBrand(
            @Parameter(description = "Brand ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody BrandRequest request) {

        BrandResponse response = brandService.updateBrand(id, request);
        String message = messageService.getMessage("success.brand.updated");
        return ResponseEntity.ok(SuccessResponse.<BrandResponse>builder()
                .message(message)
                .data(response)
                .build());
    }

    @Operation(
        summary = "[ADMIN] Delete brand",
        description = "Soft delete a brand. Requires ADMIN role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Brand successfully deleted"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role"),
        @ApiResponse(responseCode = "404", description = "Brand not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteBrand(
            @Parameter(description = "Brand ID", required = true)
            @PathVariable Long id) {
        brandService.deleteBrand(id);
        String message = messageService.getMessage("success.brand.deleted");
        return ResponseEntity.ok(Map.of("message", message));
    }
}
