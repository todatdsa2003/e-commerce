package com.ecom.product_service.mapper;

import java.util.ArrayList;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ecom.product_service.dto.ProductListDTO;
import com.ecom.product_service.model.Product;
import com.ecom.product_service.model.ProductAttribute;
import com.ecom.product_service.model.ProductImage;
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
    @Mapping(target = "thumbnailUrl", expression = "java(getThumbnailUrl(product))")
    ProductResponse toProductResponse(Product product);

    // For list view: all scalar fields map by name, attributes unused
    @Mapping(target = "attributes", expression = "java(new java.util.ArrayList<>())")
    ProductResponse toProductResponse(ProductListDTO dto);

    ProductAttributeResponse toProductAttributeResponse(ProductAttribute attribute);

    default String getThumbnailUrl(Product product) {
        return product.getImages().stream()
            .filter(img -> Boolean.TRUE.equals(img.getIsThumbnail()))
            .map(ProductImage::getImageUrl)
            .findFirst()
            .orElse(null);
    }
}
