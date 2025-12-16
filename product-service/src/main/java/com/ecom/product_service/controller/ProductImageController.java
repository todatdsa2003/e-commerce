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

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/products/{productId}/images")
@RequiredArgsConstructor
public class ProductImageController {

    private final ProductImageService productImageService;

    @GetMapping
    public ResponseEntity<List<ProductImageResponse>> getImages(@PathVariable Long productId) {
        List<ProductImageResponse> images = productImageService.getImagesByProductId(productId);
        return ResponseEntity.ok(images);
    }

    @PostMapping
    public ResponseEntity<ProductImageResponse> addImage(
            @PathVariable Long productId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "isThumbnail", defaultValue = "false") Boolean isThumbnail) {
        
        ProductImageResponse response = productImageService.addImage(productId, file, isThumbnail);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
