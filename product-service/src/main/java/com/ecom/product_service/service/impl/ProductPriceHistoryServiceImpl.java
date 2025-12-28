package com.ecom.product_service.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecom.product_service.exception.BadRequestException;
import com.ecom.product_service.exception.ResourceNotFoundException;
import com.ecom.product_service.mapper.ProductPriceHistoryMapper;
import com.ecom.product_service.model.Product;
import com.ecom.product_service.model.ProductPriceHistory;
import com.ecom.product_service.model.ProductVariant;
import com.ecom.product_service.repository.ProductPriceHistoryRepository;
import com.ecom.product_service.repository.ProductRepository;
import com.ecom.product_service.repository.ProductVariantRepository;
import com.ecom.product_service.response.PageResponse;
import com.ecom.product_service.response.ProductPriceHistoryResponse;
import com.ecom.product_service.service.ProductPriceHistoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductPriceHistoryServiceImpl implements ProductPriceHistoryService {
    private final ProductPriceHistoryRepository productPriceHistoryRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductPriceHistoryMapper productPriceHistoryMapper;
    private final MessageSource messageSource;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductPriceHistoryResponse> getPriceHistoryByProductId(Long productId, int page, int size,
                                                                                 BigDecimal minPrice, BigDecimal maxPrice) {
        Locale locale = LocaleContextHolder.getLocale();
        
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException(
                messageSource.getMessage("error.product-price-history.product-not-found", 
                    new Object[]{productId}, locale));
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

    @Override
    @Transactional
    public ProductPriceHistoryResponse createPriceHistory(Long productId, Long variantId, BigDecimal newPrice,
                                                          String changeReason, String changedBy) {
        Locale locale = LocaleContextHolder.getLocale();
        if ((productId == null && variantId == null) || (productId != null && variantId != null)) {
            throw new BadRequestException(
                messageSource.getMessage("error.price-history.product-or-variant-required", null, locale));
        }

        ProductPriceHistory priceHistory = new ProductPriceHistory();
        priceHistory.setChangeReason(changeReason);
        priceHistory.setChangedBy(changedBy);
        
        if (variantId != null) {
            ProductVariant variant = productVariantRepository.findByIdAndDeletedAtIsNull(variantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    messageSource.getMessage("error.variant.not-found", new Object[]{variantId}, locale)));

            BigDecimal oldPrice = variant.getPrice();
            
            // Check if price actually changed
            if (oldPrice.compareTo(newPrice) == 0) {
                throw new BadRequestException(
                    messageSource.getMessage("error.price-history.no-price-change", 
                        new Object[]{newPrice}, locale));
            }
            
            variant.setPrice(newPrice);
            productVariantRepository.save(variant);

            priceHistory.setVariant(variant);
            priceHistory.setOldPrice(oldPrice);
            priceHistory.setNewPrice(newPrice);

            log.info("Updated variant ID: {} price from {} to {} - Reason: {}, By: {}", 
                variantId, oldPrice, newPrice, changeReason, changedBy);
        }
        else {
            Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    messageSource.getMessage("error.product-price-history.product-not-found", 
                        new Object[]{productId}, locale)));

            BigDecimal oldPrice = product.getPrice();
            
            // Check if price actually changed
            if (oldPrice.compareTo(newPrice) == 0) {
                throw new BadRequestException(
                    messageSource.getMessage("error.price-history.no-price-change", 
                        new Object[]{newPrice}, locale));
            }
            
            product.setPrice(newPrice);
            productRepository.save(product);

            priceHistory.setProduct(product);
            priceHistory.setOldPrice(oldPrice);
            priceHistory.setNewPrice(newPrice);

            log.info("Updated product ID: {} price from {} to {} - Reason: {}, By: {}", 
                productId, oldPrice, newPrice, changeReason, changedBy);
        }

        ProductPriceHistory savedHistory = productPriceHistoryRepository.save(priceHistory);
        return productPriceHistoryMapper.toProductPriceHistoryResponse(savedHistory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductPriceHistoryResponse> getProductPriceHistory(Long productId) {
        Locale locale = LocaleContextHolder.getLocale();
        
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException(
                messageSource.getMessage("error.product-price-history.product-not-found", 
                    new Object[]{productId}, locale));
        }

        List<ProductPriceHistory> histories = productPriceHistoryRepository
            .findAllByProductIdIncludingVariants(productId);

        return histories.stream()
            .map(productPriceHistoryMapper::toProductPriceHistoryResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductPriceHistoryResponse> getVariantPriceHistory(Long variantId) {
        Locale locale = LocaleContextHolder.getLocale();
        
        if (!productVariantRepository.existsByIdAndDeletedAtIsNull(variantId)) {
            throw new ResourceNotFoundException(
                messageSource.getMessage("error.variant.not-found", new Object[]{variantId}, locale));
        }

        List<ProductPriceHistory> histories = productPriceHistoryRepository
            .findByVariantIdOrderByCreatedAtDesc(variantId);

        return histories.stream()
            .map(productPriceHistoryMapper::toProductPriceHistoryResponse)
            .collect(Collectors.toList());
    }
}
