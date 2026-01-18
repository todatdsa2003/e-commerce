package com.ecom.product_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.product_service.response.ProductImageResponse;
import com.ecom.product_service.service.ProductImageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Product Image", description = "Operations for managing product images")
@RestController
@RequestMapping("/api/v1/products/{productId}/images")
@RequiredArgsConstructor
public class ProductImageController {

    private final ProductImageService productImageService;

    @Operation(
        summary = "Get product images",
        description = "Retrieve all images associated with a specific product, including thumbnails and detail images."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved images"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping
    public ResponseEntity<List<ProductImageResponse>> getImages(
            @Parameter(description = "Unique identifier of the product", example = "1", required = true) 
            @PathVariable Long productId) {
        List<ProductImageResponse> images = productImageService.getImagesByProductId(productId);
        return ResponseEntity.ok(images);
    }

    @Operation(
        summary = "Add product image",
        description = "Upload and attach a single image to a product. Optionally mark it as the thumbnail image."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Image successfully uploaded"),
        @ApiResponse(responseCode = "400", description = "Invalid file or file type not supported"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PostMapping
    public ResponseEntity<ProductImageResponse> addImage(
            @Parameter(description = "Unique identifier of the product", example = "1", required = true) 
            @PathVariable Long productId,
            @Parameter(
                description = "Image file to upload (JPEG, PNG, or GIF)", 
                required = true,
                content = @Content(mediaType = "multipart/form-data", schema = @Schema(type = "string", format = "binary"))
            )
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "Whether this image should be the product thumbnail", example = "false") 
            @RequestParam(value = "isThumbnail", defaultValue = "false") Boolean isThumbnail) {
        
        ProductImageResponse response = productImageService.addImage(productId, file, isThumbnail);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
        summary = "Add multiple product images",
        description = "Upload and attach multiple images to a product in a single request. Useful for bulk image uploads."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Images successfully uploaded"),
        @ApiResponse(responseCode = "400", description = "Invalid files or file types not supported"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PostMapping("/multiple")
    public ResponseEntity<List<ProductImageResponse>> addMultipleImages(
            @Parameter(description = "Unique identifier of the product", example = "1", required = true) 
            @PathVariable Long productId,
            @Parameter(
                description = "List of image files to upload", 
                required = true,
                content = @Content(mediaType = "multipart/form-data")
            )
            @RequestParam("files") List<MultipartFile> files) {
        
        List<ProductImageResponse> responses = productImageService.addMultipleImages(productId, files);
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }
}
