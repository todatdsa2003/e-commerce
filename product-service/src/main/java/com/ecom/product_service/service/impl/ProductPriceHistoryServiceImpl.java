package com.ecom.product_service.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecom.product_service.exception.ResourceNotFoundException;
import com.ecom.product_service.mapper.ProductPriceHistoryMapper;
import com.ecom.product_service.model.ProductPriceHistory;
import com.ecom.product_service.repository.ProductPriceHistoryRepository;
import com.ecom.product_service.repository.ProductRepository;
import com.ecom.product_service.response.ProductPriceHistoryResponse;
import com.ecom.product_service.response.PageResponse;
import com.ecom.product_service.service.ProductPriceHistoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductPriceHistoryServiceImpl implements ProductPriceHistoryService {
    private final ProductPriceHistoryRepository productPriceHistoryRepository;
    private final ProductRepository productRepository;
    private final ProductPriceHistoryMapper productPriceHistoryMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductPriceHistoryResponse> getPriceHistoryByProductId(Long productId, int page, int size,
                                                                                 BigDecimal minPrice, BigDecimal maxPrice) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + productId);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("changedAt").descending());
        Page<ProductPriceHistory> historyPage = productPriceHistoryRepository.findByProductIdWithFilters(
                productId, minPrice, maxPrice, pageable);

        List<ProductPriceHistoryResponse> historyresponse = historyPage.getContent().stream()
                .map(productPriceHistoryMapper::toProductPriceHistoryResponse)
                .collect(Collectors.toList());

        return PageResponse.<ProductPriceHistoryResponse>builder()
                .content(historyresponse)
                .pageNumber(historyPage.getNumber())
                .pageSize(historyPage.getSize())
                .totalElements(historyPage.getTotalElements())
                .totalPages(historyPage.getTotalPages())
                .last(historyPage.isLast())
                .build();
    }
}
