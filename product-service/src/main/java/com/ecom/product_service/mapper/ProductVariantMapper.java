package com.ecom.product_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ecom.product_service.model.ProductVariant;
import com.ecom.product_service.model.ProductVariantOption;
import com.ecom.product_service.response.ProductVariantOptionResponse;
import com.ecom.product_service.response.ProductVariantResponse;

@Mapper(componentModel = "spring")
public interface ProductVariantMapper {


    ProductVariantOptionResponse toOptionResponse(ProductVariantOption option);

    @Mapping(target = "discountPercent", expression = "java(variant.getDiscountPercent())")
    @Mapping(target = "isLowStock", expression = "java(variant.isLowStock())")
    @Mapping(target = "isOutOfStock", expression = "java(variant.isOutOfStock())")
    ProductVariantResponse toVariantResponse(ProductVariant variant);
}
