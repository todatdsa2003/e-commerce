package com.ecom.product_service.mapper;

import java.util.Map;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ecom.product_service.model.ProductVariant;
import com.ecom.product_service.model.ProductVariantOption;
import com.ecom.product_service.response.ProductVariantOptionResponse;
import com.ecom.product_service.response.ProductVariantResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Mapper(componentModel = "spring")
public interface ProductVariantMapper {

    ProductVariantOptionResponse toOptionResponse(ProductVariantOption option);

    @Mapping(target = "discountPercent", expression = "java(variant.getDiscountPercent())")
    @Mapping(target = "isLowStock", expression = "java(variant.isLowStock())")
    @Mapping(target = "isOutOfStock", expression = "java(variant.isOutOfStock())")
    @Mapping(target = "optionValues", expression = "java(parseOptionValues(variant.getOptionValuesJson()))")
    ProductVariantResponse toVariantResponse(ProductVariant variant);
    
    default Map<String, String> parseOptionValues(String optionValuesJson) {
        if (optionValuesJson == null || optionValuesJson.trim().isEmpty()) {
            return null;
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(optionValuesJson, new TypeReference<Map<String, String>>() {});
        } catch (Exception e) {
            // Log error and return null if JSON parsing fails
            System.err.println("Failed to parse optionValuesJson: " + optionValuesJson + " - Error: " + e.getMessage());
            return null;
        }
    }
}
