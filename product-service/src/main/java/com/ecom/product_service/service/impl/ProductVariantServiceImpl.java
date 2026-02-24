package com.ecom.product_service.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecom.product_service.dto.BulkVariantRequest;
import com.ecom.product_service.dto.ProductVariantOptionRequest;
import com.ecom.product_service.dto.ProductVariantRequest;
import com.ecom.product_service.exception.BadRequestException;
import com.ecom.product_service.exception.ResourceNotFoundException;
import com.ecom.product_service.mapper.ProductVariantMapper;
import com.ecom.product_service.model.Product;
import com.ecom.product_service.model.ProductVariant;
import com.ecom.product_service.model.ProductVariantOption;
import com.ecom.product_service.repository.ProductRepository;
import com.ecom.product_service.repository.ProductVariantOptionRepository;
import com.ecom.product_service.repository.ProductVariantRepository;
import com.ecom.product_service.response.ProductVariantOptionResponse;
import com.ecom.product_service.response.ProductVariantResponse;
import com.ecom.product_service.response.ProductWithVariantsResponse;
import com.ecom.product_service.service.MessageService;
import com.ecom.product_service.service.ProductPriceHistoryService;
import com.ecom.product_service.service.ProductVariantService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductVariantServiceImpl implements ProductVariantService {

    private final ProductVariantRepository variantRepository;
    private final ProductVariantOptionRepository variantOptionRepository;
    private final ProductRepository productRepository;
    private final ProductVariantMapper variantMapper;
    private final MessageService messageService;
    private final ProductPriceHistoryService priceHistoryService;
    private final ObjectMapper objectMapper;

    // =========================================================================
    // VARIANT OPTION - BULK SETUP (only when no variants exist)
    // =========================================================================

    @Override
    @Transactional
    public List<ProductVariantOptionResponse> createOrUpdateVariantOptions(Long productId,
            List<ProductVariantOptionRequest> requests) {
        log.info("Creating/updating variant options for productId: {}", productId);

        Product product = findProductOrThrow(productId);

        // Block replace-all when variants already exist
        long variantCount = variantRepository.countByProductIdAndDeletedAtIsNull(productId);
        if (variantCount > 0) {
            log.error("Cannot replace options: {} variants already exist for productId: {}", variantCount, productId);
            throw new BadRequestException(
                    messageService.getMessage("error.variant.cannot-update-options-with-existing-variants"));
        }

        List<ProductVariantOption> existingOptions = variantOptionRepository
                .findByProductIdOrderByDisplayOrder(productId);
        if (!existingOptions.isEmpty()) {
            variantOptionRepository.deleteAll(existingOptions);
        }

        List<ProductVariantOption> newOptions = new ArrayList<>();
        for (int i = 0; i < requests.size(); i++) {
            ProductVariantOptionRequest request = requests.get(i);
            ProductVariantOption option = new ProductVariantOption();
            option.setProduct(product);
            option.setOptionName(request.getOptionName());
            option.setOptionValues(request.getOptionValues().toArray(new String[0]));
            option.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : i);
            newOptions.add(option);
        }

        List<ProductVariantOption> savedOptions = variantOptionRepository.saveAll(newOptions);
        log.info("Successfully created {} variant options for productId: {}", savedOptions.size(), productId);

        return savedOptions.stream()
                .map(variantMapper::toOptionResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductVariantOptionResponse> getVariantOptions(Long productId) {
        findProductOrThrow(productId);
        return variantOptionRepository.findByProductIdOrderByDisplayOrder(productId).stream()
                .map(variantMapper::toOptionResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteVariantOptions(Long productId) {
        log.info("Deleting variant options for productId: {}", productId);
        findProductOrThrow(productId);

        long variantCount = variantRepository.countByProductIdAndDeletedAtIsNull(productId);
        if (variantCount > 0) {
            throw new BadRequestException(
                    messageService.getMessage("error.variant.cannot-delete-options-with-existing-variants"));
        }

        List<ProductVariantOption> options = variantOptionRepository.findByProductIdOrderByDisplayOrder(productId);
        if (!options.isEmpty()) {
            variantOptionRepository.deleteAll(options);
            log.info("Deleted {} variant options for productId: {}", options.size(), productId);
        }
    }

    // =========================================================================
    // VARIANT OPTION - GRANULAR MANAGEMENT (allowed even when variants exist)
    // =========================================================================

    @Override
    @Transactional
    public ProductVariantOptionResponse addOptionValue(Long productId, Long optionId, String value) {
        log.info("Adding value '{}' to option {} for productId: {}", value, optionId, productId);

        findProductOrThrow(productId);
        ProductVariantOption option = findOptionOrThrow(optionId, productId);

        if (value == null || value.isBlank()) {
            throw new BadRequestException(messageService.getMessage("error.variant.option-name-null"));
        }

        boolean alreadyExists = Arrays.asList(option.getOptionValues()).contains(value);
        if (alreadyExists) {
            throw new BadRequestException(
                    messageService.getMessage("error.variant.option-value-already-exists",
                            new Object[]{value, option.getOptionName()}));
        }

        // Use native array_append() to avoid Hibernate TEXT[] tracking issues (duplicate key on save)
        variantOptionRepository.appendOptionValue(optionId, value);

        // Cache cleared by clearAutomatically=true on the @Modifying query - reload fresh from DB
        ProductVariantOption updated = variantOptionRepository.findById(optionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageService.getMessage("error.variant.option-not-found", new Object[]{optionId})));

        log.info("Added value '{}' to option '{}' for productId: {}", value, updated.getOptionName(), productId);
        return variantMapper.toOptionResponse(updated);
    }

    @Override
    @Transactional
    public ProductVariantOptionResponse removeOptionValue(Long productId, Long optionId, String value) {
        log.info("Removing value '{}' from option {} for productId: {}", value, optionId, productId);

        findProductOrThrow(productId);
        ProductVariantOption option = findOptionOrThrow(optionId, productId);

        boolean valueExists = Arrays.asList(option.getOptionValues()).contains(value);
        if (!valueExists) {
            throw new BadRequestException(
                    messageService.getMessage("error.variant.option-value-not-found",
                            new Object[]{value, option.getOptionName()}));
        }

        // Block removal if any active variant uses this value
        int usageCount = variantRepository.countVariantsUsingOptionValue(
                productId, option.getOptionName(), value);
        if (usageCount > 0) {
            throw new BadRequestException(
                    messageService.getMessage("error.variant.option-value-in-use",
                            new Object[]{value, usageCount}));
        }

        // Use native array_remove() to avoid Hibernate TEXT[] tracking issues
        variantOptionRepository.removeOptionValue(optionId, value);

        // Cache cleared by clearAutomatically=true on the @Modifying query - reload fresh from DB
        ProductVariantOption updated = variantOptionRepository.findById(optionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageService.getMessage("error.variant.option-not-found", new Object[]{optionId})));

        log.info("Removed value '{}' from option '{}' for productId: {}", value, updated.getOptionName(), productId);
        return variantMapper.toOptionResponse(updated);
    }

    @Override
    @Transactional
    public ProductVariantOptionResponse updateOptionName(Long productId, Long optionId, String newName) {
        log.info("Renaming option {} to '{}' for productId: {}", optionId, newName, productId);

        findProductOrThrow(productId);
        ProductVariantOption option = findOptionOrThrow(optionId, productId);

        if (newName == null || newName.isBlank()) {
            throw new BadRequestException(messageService.getMessage("error.variant.option-name-null"));
        }

        String oldName = option.getOptionName();

        if (oldName.equals(newName)) {
            return variantMapper.toOptionResponse(option);
        }

        // Check new name not already taken by another option of the same product
        if (variantOptionRepository.findByProductIdAndOptionName(productId, newName).isPresent()) {
            throw new BadRequestException(
                    messageService.getMessage("error.variant.option-name-already-exists",
                            new Object[]{newName}));
        }

        // Migrate option key in all existing variant JSON first
        long variantCount = variantRepository.countByProductIdAndDeletedAtIsNull(productId);
        if (variantCount > 0) {
            variantRepository.renameOptionInVariants(productId, oldName, newName);
            log.info("Renamed option key '{}' -> '{}' in {} variants for productId: {}",
                    oldName, newName, variantCount, productId);
        }

        // Use native UPDATE to rename the option - avoid Hibernate TEXT[] save issues
        variantOptionRepository.updateOptionName(optionId, newName);

        // Cache cleared by clearAutomatically=true on the @Modifying query - reload fresh from DB
        ProductVariantOption updated = variantOptionRepository.findById(optionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageService.getMessage("error.variant.option-not-found", new Object[]{optionId})));

        log.info("Renamed option '{}' -> '{}' for productId: {}", oldName, newName, productId);
        return variantMapper.toOptionResponse(updated);
    }

    // =========================================================================
    // VARIANT CRUD
    // =========================================================================

    @Override
    @Transactional
    public ProductVariantResponse createVariant(Long productId, ProductVariantRequest request) {
        log.info("Creating variant for productId: {}, SKU: {}", productId, request.getSku());

        Product product = findProductOrThrow(productId);
        validateSkuUnique(request.getSku(), null);

        List<ProductVariantOption> orderedOptions = validateOptionValues(productId, request.getOptionValues());

        ProductVariant variant = new ProductVariant();
        variant.setProduct(product);
        variant.setSku(request.getSku());
        variant.setVariantName(generateVariantName(request.getOptionValues(), orderedOptions));
        variant.setOptionValuesJson(convertMapToJson(request.getOptionValues()));
        variant.setPrice(request.getPrice());
        variant.setCompareAtPrice(request.getCompareAtPrice());
        variant.setStockQuantity(request.getStockQuantity());
        variant.setLowStockThreshold(request.getLowStockThreshold());
        variant.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        variant.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);

        long existingCount = variantRepository.countByProductIdAndDeletedAtIsNull(productId);
        boolean shouldBeDefault = existingCount == 0 || Boolean.TRUE.equals(request.getIsDefault());

        if (shouldBeDefault) {
            unsetCurrentDefaultVariant(productId);
            variant.setIsDefault(true);
        } else {
            variant.setIsDefault(false);
        }

        ProductVariant saved = variantRepository.save(variant);
        log.info("Created variant ID: {} for productId: {}", saved.getId(), productId);

        return variantMapper.toVariantResponse(saved);
    }

    @Override
    @Transactional
    public List<ProductVariantResponse> createVariantsBulk(Long productId, BulkVariantRequest request) {
        log.info("Bulk creating {} variants for productId: {}", request.getVariants().size(), productId);

        if (request.getOptions() != null && !request.getOptions().isEmpty()) {
            createOrUpdateVariantOptions(productId, request.getOptions());
        }

        List<ProductVariantResponse> created = new ArrayList<>();
        for (ProductVariantRequest variantRequest : request.getVariants()) {
            created.add(createVariant(productId, variantRequest));
        }

        log.info("Bulk created {} variants for productId: {}", created.size(), productId);
        return created;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductVariantResponse> getVariants(Long productId, Boolean activeOnly, boolean includeDeleted) {
        findProductOrThrow(productId);

        List<ProductVariant> variants;
        if (includeDeleted) {
            variants = variantRepository.findByProductIdOrderByDisplayOrder(productId);
        } else if (Boolean.TRUE.equals(activeOnly)) {
            variants = variantRepository
                    .findByProductIdAndIsActiveTrueAndDeletedAtIsNullOrderByDisplayOrder(productId);
        } else {
            variants = variantRepository.findByProductIdAndDeletedAtIsNullOrderByDisplayOrder(productId);
        }

        return variants.stream().map(variantMapper::toVariantResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductVariantResponse getVariantById(Long variantId) {
        return variantMapper.toVariantResponse(findVariantOrThrow(variantId));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductVariantResponse getDefaultVariant(Long productId) {
        findProductOrThrow(productId);
        return variantMapper.toVariantResponse(
                variantRepository.findByProductIdAndIsDefaultTrueAndDeletedAtIsNull(productId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                messageService.getMessage("error.variant.default-not-found"))));
    }

    @Override
    @Transactional
    public ProductVariantResponse updateVariant(Long variantId, ProductVariantRequest request) {
        log.info("Updating variant ID: {}", variantId);

        ProductVariant variant = findVariantOrThrow(variantId);

        if (!variant.getSku().equals(request.getSku())) {
            validateSkuUnique(request.getSku(), variantId);
        }

        if (request.getOptionValues() != null && !request.getOptionValues().isEmpty()) {
            List<ProductVariantOption> orderedOptions =
                    validateOptionValues(variant.getProduct().getId(), request.getOptionValues());
            variant.setVariantName(generateVariantName(request.getOptionValues(), orderedOptions));
            variant.setOptionValuesJson(convertMapToJson(request.getOptionValues()));
        }

        BigDecimal oldPrice = variant.getPrice();
        BigDecimal newPrice = request.getPrice();

        variant.setSku(request.getSku());
        variant.setPrice(newPrice);
        variant.setCompareAtPrice(request.getCompareAtPrice());
        variant.setStockQuantity(request.getStockQuantity());
        variant.setLowStockThreshold(request.getLowStockThreshold());
        variant.setDisplayOrder(
                request.getDisplayOrder() != null ? request.getDisplayOrder() : variant.getDisplayOrder());
        variant.setIsActive(request.getIsActive() != null ? request.getIsActive() : variant.getIsActive());

        if (Boolean.TRUE.equals(request.getIsDefault()) && !variant.getIsDefault()) {
            unsetCurrentDefaultVariant(variant.getProduct().getId());
            variant.setIsDefault(true);
        } else if (Boolean.FALSE.equals(request.getIsDefault()) && variant.getIsDefault()) {
            log.warn("Ignoring request to unset default variant ID: {} without assigning a new default", variantId);
        }

        ProductVariant updated = variantRepository.save(variant);

        if (oldPrice.compareTo(newPrice) != 0) {
            priceHistoryService.createPriceHistory(null, variantId, newPrice,
                    "Price updated via variant update", "SYSTEM");
            log.info("Price history created for variant ID: {} ({} -> {})", variantId, oldPrice, newPrice);
        }

        log.info("Updated variant ID: {}", variantId);
        return variantMapper.toVariantResponse(updated);
    }

    @Override
    @Transactional
    public void deleteVariant(Long variantId) {
        log.info("Soft deleting variant ID: {}", variantId);

        ProductVariant variant = findVariantOrThrow(variantId);
        boolean wasDefault = variant.getIsDefault();
        Long productId = variant.getProduct().getId();

        variant.softDelete();
        variantRepository.save(variant);

        if (wasDefault) {
            variantRepository
                    .findByProductIdAndIsActiveTrueAndDeletedAtIsNullOrderByDisplayOrder(productId)
                    .stream().findFirst()
                    .ifPresent(newDefault -> {
                        newDefault.setIsDefault(true);
                        variantRepository.save(newDefault);
                        log.info("Promoted variant ID: {} to default after deleting ID: {}",
                                newDefault.getId(), variantId);
                    });
        }
    }

    @Override
    @Transactional
    public ProductVariantResponse updateStock(Long variantId, Integer newStock) {
        log.info("Updating stock for variant ID: {} to {}", variantId, newStock);

        // Fix: explicit null check before unboxing
        if (newStock == null || newStock < 0) {
            throw new BadRequestException(messageService.getMessage("error.variant.stock-invalid"));
        }

        ProductVariant variant = findVariantOrThrow(variantId);
        variant.setStockQuantity(newStock);
        ProductVariant updated = variantRepository.save(variant);

        log.info("Updated stock for variant ID: {} to {}", variantId, newStock);
        return variantMapper.toVariantResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductWithVariantsResponse getProductWithVariants(Long productId, boolean includeDeleted) {
        Product product = findProductOrThrow(productId);

        List<ProductVariantOption> options = variantOptionRepository.findByProductIdOrderByDisplayOrder(productId);

        List<ProductVariant> variants = includeDeleted
                ? variantRepository.findByProductIdOrderByDisplayOrder(productId)
                : variantRepository.findByProductIdAndDeletedAtIsNullOrderByDisplayOrder(productId);

        ProductVariant defaultVariant = variantRepository
                .findByProductIdAndIsDefaultTrueAndDeletedAtIsNull(productId)
                .orElse(null);

        // Fix: totalVariants = all non-deleted (incl. inactive), activeVariants = active only
        long totalVariants = variantRepository.countAllVariantsByProductId(productId);
        Integer activeVariants = variantRepository.countActiveVariantsByProductId(productId);
        BigDecimal minPrice = variantRepository.findMinPriceByProductId(productId);
        BigDecimal maxPrice = variantRepository.findMaxPriceByProductId(productId);
        Integer totalStock = variantRepository.sumStockByProductId(productId);

        return ProductWithVariantsResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .description(product.getDescription())
                .shortDescription(product.getDescription() != null && product.getDescription().length() > 100
                        ? product.getDescription().substring(0, 100) + "..."
                        : product.getDescription())
                .variantOptions(options.stream()
                        .map(variantMapper::toOptionResponse)
                        .collect(Collectors.toList()))
                .variants(variants.stream()
                        .map(variantMapper::toVariantResponse)
                        .collect(Collectors.toList()))
                .defaultVariant(defaultVariant != null ? variantMapper.toVariantResponse(defaultVariant) : null)
                .totalVariants((int) totalVariants)
                .activeVariants(activeVariants)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .totalStock(totalStock != null ? totalStock : 0)
                .hasVariants(totalVariants > 0)
                .build();
    }

    // =========================================================================
    // PRIVATE HELPERS
    // =========================================================================

    private Product findProductOrThrow(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageService.getMessage("error.product.not-found", new Object[]{productId})));
    }

    private ProductVariant findVariantOrThrow(Long variantId) {
        return variantRepository.findById(variantId)
                .filter(v -> v.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageService.getMessage("error.variant.not-found", new Object[]{variantId})));
    }

    private ProductVariantOption findOptionOrThrow(Long optionId, Long productId) {
        return variantOptionRepository.findById(optionId)
                .filter(o -> o.getProduct().getId().equals(productId))
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageService.getMessage("error.variant.option-not-found", new Object[]{optionId})));
    }

    private void validateSkuUnique(String sku, Long excludeId) {
        variantRepository.findBySkuAndDeletedAtIsNull(sku).ifPresent(existing -> {
            if (excludeId == null || !existing.getId().equals(excludeId)) {
                throw new BadRequestException(
                        messageService.getMessage("error.variant.sku-exists", new Object[]{sku}));
            }
        });
    }

    /**
     * Validates option values map against product's defined options.
     * Checks:
     *   - All defined option names have a value in the request (no missing options).
     *   - No extra option names exist in the request.
     *   - Each value is valid for its option.
     *
     * @return the ordered list of product options (avoids extra DB call in caller)
     */
    private List<ProductVariantOption> validateOptionValues(Long productId, Map<String, String> optionValues) {
        if (optionValues == null || optionValues.isEmpty()) {
            throw new BadRequestException(messageService.getMessage("error.variant.options-required"));
        }

        List<ProductVariantOption> productOptions = variantOptionRepository
                .findByProductIdOrderByDisplayOrder(productId);

        if (productOptions.isEmpty()) {
            throw new BadRequestException(messageService.getMessage("error.variant.no-options-defined"));
        }

        // Check 1: All defined options must have a value in the request
        for (ProductVariantOption defined : productOptions) {
            if (!optionValues.containsKey(defined.getOptionName())) {
                throw new BadRequestException(
                        messageService.getMessage("error.variant.missing-required-option",
                                new Object[]{defined.getOptionName()}));
            }
        }

        // Check 2: All keys in request must match a defined option, and value must be valid
        for (Map.Entry<String, String> entry : optionValues.entrySet()) {
            String optionName = entry.getKey();
            String optionValue = entry.getValue();

            ProductVariantOption matchingOption = productOptions.stream()
                    .filter(opt -> opt.getOptionName().equals(optionName))
                    .findFirst()
                    .orElseThrow(() -> new BadRequestException(
                            messageService.getMessage("error.variant.option-mismatch")));

            boolean valueValid = Arrays.asList(matchingOption.getOptionValues()).contains(optionValue);
            if (!valueValid) {
                throw new BadRequestException(
                        messageService.getMessage("error.variant.invalid-option-value",
                                new Object[]{optionName, optionValue}));
            }
        }

        return productOptions;
    }

    /**
     * Generates variant display name from option values, preserving option displayOrder.
     * E.g., Color=Red, Size=M (Color displayOrder=0, Size displayOrder=1) → "Red / M"
     */
    private String generateVariantName(Map<String, String> optionValues,
            List<ProductVariantOption> orderedOptions) {
        if (optionValues == null || optionValues.isEmpty()) {
            return "Default";
        }
        return orderedOptions.stream()
                .map(opt -> optionValues.get(opt.getOptionName()))
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" / "));
    }

    private void unsetCurrentDefaultVariant(Long productId) {
        variantRepository.findByProductIdAndIsDefaultTrueAndDeletedAtIsNull(productId)
                .ifPresent(current -> {
                    current.setIsDefault(false);
                    variantRepository.save(current);
                });
    }

    /**
     * Serializes option values map to JSON using Jackson.
     * Safe: handles special characters like quotes and backslashes correctly.
     */
    private String convertMapToJson(Map<String, String> optionValues) {
        try {
            return objectMapper.writeValueAsString(optionValues != null ? optionValues : Map.of());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize option values", e);
            throw new BadRequestException(messageService.getMessage("error.variant.invalid-option-values"));
        }
    }
}
