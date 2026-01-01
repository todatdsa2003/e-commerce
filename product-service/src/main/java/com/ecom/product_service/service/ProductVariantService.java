package com.ecom.product_service.service;

import java.util.List;

import com.ecom.product_service.dto.BulkVariantRequest;
import com.ecom.product_service.dto.ProductVariantOptionRequest;
import com.ecom.product_service.dto.ProductVariantRequest;
import com.ecom.product_service.response.ProductVariantOptionResponse;
import com.ecom.product_service.response.ProductVariantResponse;
import com.ecom.product_service.response.ProductWithVariantsResponse;

public interface ProductVariantService {

    List<ProductVariantOptionResponse> createOrUpdateVariantOptions(Long productId,
            List<ProductVariantOptionRequest> requests);
    List<ProductVariantOptionResponse> getVariantOptions(Long productId);
    void deleteVariantOptions(Long productId);
    ProductVariantResponse createVariant(Long productId, ProductVariantRequest request);
    List<ProductVariantResponse> createVariantsBulk(Long productId, BulkVariantRequest request);
    List<ProductVariantResponse> getVariants(Long productId, Boolean activeOnly);
    ProductVariantResponse getVariantById(Long variantId);
    ProductVariantResponse getDefaultVariant(Long productId);
    ProductVariantResponse updateVariant(Long variantId, ProductVariantRequest request);
    void deleteVariant(Long variantId);
    ProductVariantResponse updateStock(Long variantId, Integer newStock);
    ProductWithVariantsResponse getProductWithVariants(Long productId);
}
