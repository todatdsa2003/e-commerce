package com.ecom.product_service.service;

import com.ecom.product_service.dto.ProductRequest;
import com.ecom.product_service.response.CreateProductResponse;
import com.ecom.product_service.response.PageResponse;
import com.ecom.product_service.response.ProductResponse;

public interface ProductService {

    PageResponse<ProductResponse> getAllProducts(int page, int size, String search, Long statusId, Long categoryId, Long brandId, boolean includeDeleted);

    ProductResponse getProductById(Long id, boolean includeDeleted);

    CreateProductResponse createProduct(ProductRequest request);

    ProductResponse updateProduct(Long id, ProductRequest request);

    void deleteProduct(Long id);
}
