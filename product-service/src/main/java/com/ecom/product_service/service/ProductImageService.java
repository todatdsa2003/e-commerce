package com.ecom.product_service.service;

import org.springframework.web.multipart.MultipartFile;

import com.ecom.product_service.response.ProductImageResponse;

public interface ProductImageService {
    
    ProductImageResponse addImage(Long productId, MultipartFile file, Boolean isThumbnail);
}
