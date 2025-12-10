package com.ecom.product_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.product_service.responses.ProductStatusResponse;
import com.ecom.product_service.service.ProductStatusService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/product-status")
@RequiredArgsConstructor
public class ProductStatusController {

    private final ProductStatusService productStatusService;

    @GetMapping
    public ResponseEntity<List<ProductStatusResponse>> getAllProductStatuses() {
        List<ProductStatusResponse> response = productStatusService.getAllProductStatuses();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductStatusResponse> getProductStatusById(@PathVariable Long id) {
        ProductStatusResponse response = productStatusService.getProductStatusById(id);
        return ResponseEntity.ok(response);
    }
}
