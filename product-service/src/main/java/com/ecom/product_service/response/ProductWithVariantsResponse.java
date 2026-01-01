package com.ecom.product_service.response;

import java.math.BigDecimal;
import java.util.List;

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
public class ProductWithVariantsResponse {

    private Long id;
    private String name;
    private String slug;
    private String description;
    private String shortDescription;

    private List<ProductVariantOptionResponse> variantOptions;
    private List<ProductVariantResponse> variants;
    private ProductVariantResponse defaultVariant;

    private Integer totalVariants;
    private Integer activeVariants;
    private Integer totalStock;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Boolean hasVariants;
}
