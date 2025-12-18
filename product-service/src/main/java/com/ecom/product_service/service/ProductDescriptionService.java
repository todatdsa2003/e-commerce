package com.ecom.product_service.service;

import com.ecom.product_service.dto.ProductDescriptionRequest;
import com.ecom.product_service.response.ProductDescriptionResponse;

public interface ProductDescriptionService {
    
    ProductDescriptionResponse generateDescription(ProductDescriptionRequest request);
}
