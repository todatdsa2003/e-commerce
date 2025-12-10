package com.ecom.product_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ecom.product_service.model.Brand;
import com.ecom.product_service.responses.BrandResponse;

@Mapper(componentModel = "spring")
public interface BrandMapper {
    
    @Mapping(target = "productCount", expression = "java(productCount)")
    BrandResponse toBrandResponse(Brand brand, Long productCount);
}
