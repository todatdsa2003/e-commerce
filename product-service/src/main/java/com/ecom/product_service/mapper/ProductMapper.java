package com.ecom.product_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ecom.product_service.model.Product;
import com.ecom.product_service.model.ProductAttribute;
import com.ecom.product_service.response.ProductAttributeResponse;
import com.ecom.product_service.response.ProductResponse;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "status.id", target = "statusId")
    @Mapping(source = "status.label", target = "statusName")
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "brand.id", target = "brandId")
    @Mapping(source = "brand.name", target = "brandName")
    ProductResponse toProductResponse(Product product);
    
    ProductAttributeResponse toProductAttributeResponse(ProductAttribute attribute);
}
