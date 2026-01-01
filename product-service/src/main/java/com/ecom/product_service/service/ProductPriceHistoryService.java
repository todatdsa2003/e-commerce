package com.ecom.product_service.service;

import java.math.BigDecimal;
import java.util.List;

import com.ecom.product_service.response.PageResponse;
import com.ecom.product_service.response.ProductPriceHistoryResponse;

public interface ProductPriceHistoryService {
    
    PageResponse<ProductPriceHistoryResponse> getPriceHistoryByProductId(Long productId, int page, int size, 
                                                                          BigDecimal minPrice, BigDecimal maxPrice);

    ProductPriceHistoryResponse createPriceHistory(Long productId, Long variantId, BigDecimal newPrice, 
                                                    String changeReason, String changedBy);

    List<ProductPriceHistoryResponse> getProductPriceHistory(Long productId);

    List<ProductPriceHistoryResponse> getVariantPriceHistory(Long variantId);
}
