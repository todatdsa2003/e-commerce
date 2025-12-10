package com.ecom.product_service.service;

import com.ecom.product_service.dto.BrandRequest;
import com.ecom.product_service.responses.BrandResponse;
import com.ecom.product_service.responses.PageResponse;

public interface BrandService {
    PageResponse<BrandResponse> getAllBrands(int page, int size, String search);
    BrandResponse getBrandById(Long brandId);
    BrandResponse createBrand(BrandRequest request);
    BrandResponse updateBrand(Long brandId, BrandRequest request);
    void deleteBrand(Long brandId);
}
