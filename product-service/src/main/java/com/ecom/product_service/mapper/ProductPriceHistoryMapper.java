package com.ecom.product_service.mapper;

import java.math.BigDecimal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ecom.product_service.model.ProductPriceHistory;
import com.ecom.product_service.response.ProductPriceHistoryResponse;

@Mapper(componentModel = "spring")
public interface ProductPriceHistoryMapper {

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(target = "priceChange", expression = "java(calculatePriceChange(priceHistory))")
    ProductPriceHistoryResponse toProductPriceHistoryResponse(ProductPriceHistory priceHistory);
    
    default BigDecimal calculatePriceChange(ProductPriceHistory priceHistory) {
        if (priceHistory.getNewPrice() != null && priceHistory.getOldPrice() != null) {
            return priceHistory.getNewPrice().subtract(priceHistory.getOldPrice());
        }
        return BigDecimal.ZERO;
    }
}
