package com.ecom.product_service.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ecom.product_service.response.ProductImageResponse;

public interface ProductImageService {
    
    ProductImageResponse addImage(Long productId, MultipartFile file, Boolean isThumbnail);
    
    List<ProductImageResponse> addMultipleImages(Long productId, List<MultipartFile> files);
    
    List<ProductImageResponse> getImagesByProductId(Long productId);
}
