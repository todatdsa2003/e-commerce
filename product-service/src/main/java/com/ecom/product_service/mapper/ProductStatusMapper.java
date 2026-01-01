package com.ecom.product_service.mapper;

import org.mapstruct.Mapper;

import com.ecom.product_service.model.ProductStatus;
import com.ecom.product_service.response.ProductStatusResponse;

@Mapper(componentModel = "spring")
public interface ProductStatusMapper {
    
    ProductStatusResponse toProductStatusResponse(ProductStatus productStatus);
}
