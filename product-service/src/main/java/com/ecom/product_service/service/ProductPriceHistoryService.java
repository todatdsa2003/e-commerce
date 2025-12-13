package com.ecom.product_service.service;

import java.math.BigDecimal;

import com.ecom.product_service.response.ProductPriceHistoryResponse;
import com.ecom.product_service.response.PageResponse;

public interface ProductPriceHistoryService {
    
    PageResponse<ProductPriceHistoryResponse> getPriceHistoryByProductId(Long productId, int page, int size, 
                                                                          BigDecimal minPrice, BigDecimal maxPrice);
}
