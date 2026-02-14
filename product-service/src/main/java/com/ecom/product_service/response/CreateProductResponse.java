package com.ecom.product_service.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductResponse {

    private ProductResponse product;

    private List<ProductImageResponse> images;

    private List<ProductVariantOptionResponse> variantOptions;

    private List<ProductVariantResponse> variants;
}
