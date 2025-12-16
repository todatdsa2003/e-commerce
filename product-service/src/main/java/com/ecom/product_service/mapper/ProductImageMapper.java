package com.ecom.product_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ecom.product_service.model.ProductImage;
import com.ecom.product_service.response.ProductImageResponse;

@Mapper(componentModel = "spring")
public interface ProductImageMapper {
    
    @Mapping(target = "productId", source = "product.id")
    ProductImageResponse toProductImageResponse(ProductImage productImage);
}
