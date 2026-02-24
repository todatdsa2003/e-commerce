package com.ecom.product_service.service;

import java.util.List;

import com.ecom.product_service.dto.BulkVariantRequest;
import com.ecom.product_service.dto.ProductVariantOptionRequest;
import com.ecom.product_service.dto.ProductVariantRequest;
import com.ecom.product_service.response.ProductVariantOptionResponse;
import com.ecom.product_service.response.ProductVariantResponse;
import com.ecom.product_service.response.ProductWithVariantsResponse;

public interface ProductVariantService {

    // --- Variant Option bulk setup (only allowed when no variants exist) ---
    List<ProductVariantOptionResponse> createOrUpdateVariantOptions(Long productId,
            List<ProductVariantOptionRequest> requests);
    List<ProductVariantOptionResponse> getVariantOptions(Long productId);
    void deleteVariantOptions(Long productId);

    // --- Granular option value management (allowed even when variants exist) ---
    ProductVariantOptionResponse addOptionValue(Long productId, Long optionId, String value);
    ProductVariantOptionResponse removeOptionValue(Long productId, Long optionId, String value);
    ProductVariantOptionResponse updateOptionName(Long productId, Long optionId, String newName);

    // --- Variant CRUD ---
    ProductVariantResponse createVariant(Long productId, ProductVariantRequest request);
    List<ProductVariantResponse> createVariantsBulk(Long productId, BulkVariantRequest request);
    List<ProductVariantResponse> getVariants(Long productId, Boolean activeOnly, boolean includeDeleted);
    ProductVariantResponse getVariantById(Long variantId);
    ProductVariantResponse getDefaultVariant(Long productId);
    ProductVariantResponse updateVariant(Long variantId, ProductVariantRequest request);
    void deleteVariant(Long variantId);
    ProductVariantResponse updateStock(Long variantId, Integer newStock);
    ProductWithVariantsResponse getProductWithVariants(Long productId, boolean includeDeleted);
}
