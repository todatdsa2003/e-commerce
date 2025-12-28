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
    @Mapping(source = "variant.id", target = "variantId")
    @Mapping(source = "variant.sku", target = "variantSku")
    @Mapping(source = "variant.variantName", target = "variantName")
    @Mapping(target = "entityType", expression = "java(priceHistory.getEntityType())")
    @Mapping(target = "priceChange", expression = "java(calculatePriceChange(priceHistory))")
    @Mapping(source = "changeReason", target = "changeReason")
    @Mapping(source = "changedBy", target = "changedBy")
    ProductPriceHistoryResponse toProductPriceHistoryResponse(ProductPriceHistory priceHistory);
    
    default BigDecimal calculatePriceChange(ProductPriceHistory priceHistory) {
        if (priceHistory.getNewPrice() != null && priceHistory.getOldPrice() != null) {
            return priceHistory.getNewPrice().subtract(priceHistory.getOldPrice());
        }
        return BigDecimal.ZERO;
    }
}
