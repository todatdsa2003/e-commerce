package com.ecom.product_service.service;

import com.ecom.product_service.dto.CategoryRequest;
import com.ecom.product_service.responses.CategoryResponse;
import com.ecom.product_service.responses.PageResponse;

public interface CategoryService {
    PageResponse<CategoryResponse> getAllCategories(int page, int size, String search);
    CategoryResponse getCategoryById(Long categoryId);
    CategoryResponse createCategory(CategoryRequest request);
    CategoryResponse updateCategory(Long categoryId,CategoryRequest request);
    void deleteCategory(Long categoryId);

}
