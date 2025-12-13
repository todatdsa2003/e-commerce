package com.ecom.product_service.controller;

import java.math.BigDecimal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.product_service.response.ProductPriceHistoryResponse;
import com.ecom.product_service.response.PageResponse;
import com.ecom.product_service.service.ProductPriceHistoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/products/{productId}/price-history")
@RequiredArgsConstructor
public class ProductPriceHistoryController {

    private final ProductPriceHistoryService productPriceHistoryService;

    @GetMapping
    public ResponseEntity<PageResponse<ProductPriceHistoryResponse>> getPriceHistory(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {

        PageResponse<ProductPriceHistoryResponse> response = productPriceHistoryService.getPriceHistoryByProductId(
                productId, page, size, minPrice, maxPrice);
        return ResponseEntity.ok(response);
    }
}
