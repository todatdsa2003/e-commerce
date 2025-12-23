package com.ecom.product_service.response;

import java.math.BigDecimal;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariantResponse {

    private Long id;
    private String sku;
    private String variantName;

    private BigDecimal price;
    private BigDecimal compareAtPrice;
    private BigDecimal discountPercent;

    private Integer stockQuantity;
    private Integer lowStockThreshold;
    private Boolean isLowStock;
    private Boolean isOutOfStock;

    private Map<String, String> optionValues;

    private Boolean isDefault;
    private Boolean isActive;
    private Integer displayOrder;
}
