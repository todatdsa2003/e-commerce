package com.ecom.product_service.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecom.product_service.exception.ResourceNotFoundException;
import com.ecom.product_service.mapper.ProductStatusMapper;
import com.ecom.product_service.model.ProductStatus;
import com.ecom.product_service.repository.ProductStatusRepository;
import com.ecom.product_service.response.ProductStatusResponse;
import com.ecom.product_service.service.MessageService;
import com.ecom.product_service.service.ProductStatusService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductStatusServiceImpl implements ProductStatusService {
    private final ProductStatusRepository productStatusRepository;
    private final ProductStatusMapper productStatusMapper;
    private final MessageService messageService;

    @Override
    @Transactional(readOnly = true)
    public List<ProductStatusResponse> getAllProductStatuses() {
        List<ProductStatus> statuses = productStatusRepository.findAllOrderByDisplayOrder();

        return statuses.stream()
                .map(productStatusMapper::toProductStatusResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductStatusResponse getProductStatusById(Long id) {
        ProductStatus status = productStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    messageService.getMessage("error.product-status.not-found", new Object[]{id})));

        return productStatusMapper.toProductStatusResponse(status);
    }
}

