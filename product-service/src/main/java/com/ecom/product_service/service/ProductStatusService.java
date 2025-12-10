package com.ecom.product_service.service;

import java.util.List;

import com.ecom.product_service.responses.ProductStatusResponse;

public interface ProductStatusService {
    List<ProductStatusResponse> getAllProductStatuses();
    ProductStatusResponse getProductStatusById(Long statusId);
}
