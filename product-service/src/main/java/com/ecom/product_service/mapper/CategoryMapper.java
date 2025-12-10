package com.ecom.product_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ecom.product_service.model.Category;
import com.ecom.product_service.responses.CategoryResponse;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    
    @Mapping(target = "parentName", ignore = true)
    @Mapping(target = "children", ignore = true)
    CategoryResponse toCategoryResponse(Category category);
}
