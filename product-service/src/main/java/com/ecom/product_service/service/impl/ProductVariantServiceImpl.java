package com.ecom.product_service.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import com.ecom.product_service.service.ProductVariantService;

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

    @Override
    @Transactional
    public List<ProductVariantOptionResponse> createOrUpdateVariantOptions(Long productId,
            List<ProductVariantOptionRequest> requests) {
        log.info("Creating/updating variant options for productId: {}", productId);

        // Validate product exists
        Product product = findProductOrThrow(productId);

        // Check if variants already exist
        long variantCount = variantRepository.countActiveVariantsByProductId(productId);
        if (variantCount > 0) {
            log.error("Cannot update options: {} variants already exist for productId: {}", variantCount, productId);
            throw new BadRequestException(
                    messageService.getMessage("variant.options.cannot.update.variants.exist"));
        }

        // Delete existing options
        List<ProductVariantOption> existingOptions = variantOptionRepository.findByProductIdOrderByDisplayOrder(
                productId);
        if (!existingOptions.isEmpty()) {
            log.info("Deleting {} existing options for productId: {}", existingOptions.size(), productId);
            variantOptionRepository.deleteAll(existingOptions);
        }

        // Create new options
        List<ProductVariantOption> newOptions = new ArrayList<>();
        for (int i = 0; i < requests.size(); i++) {
            ProductVariantOptionRequest request = requests.get(i);
            ProductVariantOption option = new ProductVariantOption();
            option.setProduct(product);
            option.setOptionName(request.getOptionName());
            option.setOptionValues(request.getOptionValues().toArray(new String[0]));
            option.setDisplayOrder(i);
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
        log.info("Fetching variant options for productId: {}", productId);

        // Validate product exists
        findProductOrThrow(productId);

        List<ProductVariantOption> options = variantOptionRepository.findByProductIdOrderByDisplayOrder(productId);
        log.info("Found {} variant options for productId: {}", options.size(), productId);

        return options.stream()
                .map(variantMapper::toOptionResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteVariantOptions(Long productId) {
        log.info("Deleting variant options for productId: {}", productId);

        // Validate product exists
        findProductOrThrow(productId);

        // Check if variants already exist
        long variantCount = variantRepository.countActiveVariantsByProductId(productId);
        if (variantCount > 0) {
            log.error("Cannot delete options: {} variants exist for productId: {}", variantCount, productId);
            throw new BadRequestException(
                    messageService.getMessage("variant.options.cannot.delete.variants.exist"));
        }

        List<ProductVariantOption> options = variantOptionRepository.findByProductIdOrderByDisplayOrder(productId);
        if (!options.isEmpty()) {
            variantOptionRepository.deleteAll(options);
            log.info("Successfully deleted {} variant options for productId: {}", options.size(), productId);
        } else {
            log.info("No variant options found to delete for productId: {}", productId);
        }
    }

    @Override
    @Transactional
    public ProductVariantResponse createVariant(Long productId, ProductVariantRequest request) {
        log.info("Creating variant for productId: {}, SKU: {}", productId, request.getSku());

        // Validate product exists
        Product product = findProductOrThrow(productId);

        // Validate SKU unique
        validateSkuUnique(request.getSku(), null);

        // Validate option values match product's variant options
        validateOptionValues(productId, request.getOptionValues());

        // Create variant entity
        ProductVariant variant = new ProductVariant();
        variant.setProduct(product);
        variant.setSku(request.getSku());
        variant.setVariantName(generateVariantName(request.getOptionValues()));
        variant.setOptionValuesJson(convertMapToJson(request.getOptionValues()));
        variant.setPrice(request.getPrice());
        variant.setCompareAtPrice(request.getCompareAtPrice());
        variant.setStockQuantity(request.getStockQuantity());
        variant.setLowStockThreshold(request.getLowStockThreshold());
        variant.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        variant.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);

        // Set as default if it's the first variant OR explicitly requested
        long existingVariants = variantRepository.countActiveVariantsByProductId(productId);
        boolean shouldBeDefault = existingVariants == 0 || Boolean.TRUE.equals(request.getIsDefault());

        if (shouldBeDefault) {
            // Unset current default variant if exists
            unsetCurrentDefaultVariant(productId);
            variant.setIsDefault(true);
            log.info("Setting variant as default for productId: {}", productId);
        } else {
            variant.setIsDefault(false);
        }

        ProductVariant savedVariant = variantRepository.save(variant);
        log.info("Successfully created variant with ID: {} for productId: {}", savedVariant.getId(), productId);

        return variantMapper.toVariantResponse(savedVariant);
    }

    @Override
    @Transactional
    public List<ProductVariantResponse> createVariantsBulk(Long productId, BulkVariantRequest request) {
        log.info("Creating {} variants in bulk for productId: {}", request.getVariants().size(), productId);

        // Create or update variant options first
        if (request.getOptions() != null && !request.getOptions().isEmpty()) {
            createOrUpdateVariantOptions(productId, request.getOptions());
        }

        // Create all variants
        List<ProductVariantResponse> createdVariants = new ArrayList<>();
        for (ProductVariantRequest variantRequest : request.getVariants()) {
            ProductVariantResponse created = createVariant(productId, variantRequest);
            createdVariants.add(created);
        }

        log.info("Successfully created {} variants in bulk for productId: {}", createdVariants.size(), productId);
        return createdVariants;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductVariantResponse> getVariants(Long productId, Boolean activeOnly) {
        log.info("Fetching variants for productId: {}, activeOnly: {}", productId, activeOnly);

        // Validate product exists
        findProductOrThrow(productId);

        List<ProductVariant> variants;
        if (Boolean.TRUE.equals(activeOnly)) {
            variants = variantRepository.findByProductIdAndIsActiveTrueAndDeletedAtIsNullOrderByDisplayOrder(productId);
        } else {
            variants = variantRepository.findByProductIdAndDeletedAtIsNullOrderByDisplayOrder(productId);
        }

        log.info("Found {} variants for productId: {}", variants.size(), productId);
        return variants.stream()
                .map(variantMapper::toVariantResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductVariantResponse getVariantById(Long variantId) {
        log.info("Fetching variant by ID: {}", variantId);

        ProductVariant variant = findVariantOrThrow(variantId);
        return variantMapper.toVariantResponse(variant);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductVariantResponse getDefaultVariant(Long productId) {
        log.info("Fetching default variant for productId: {}", productId);

        // Validate product exists
        findProductOrThrow(productId);

        ProductVariant defaultVariant = variantRepository.findByProductIdAndIsDefaultTrueAndDeletedAtIsNull(productId)
                .orElseThrow(() -> {
                    log.error("Default variant not found for productId: {}", productId);
                    return new ResourceNotFoundException(
                            messageService.getMessage("variant.default.not.found"));
                });

        return variantMapper.toVariantResponse(defaultVariant);
    }

    @Override
    @Transactional
    public ProductVariantResponse updateVariant(Long variantId, ProductVariantRequest request) {
        log.info("Updating variant ID: {}", variantId);

        // Find existing variant
        ProductVariant variant = findVariantOrThrow(variantId);

        // Validate SKU unique (excluding current variant)
        if (!variant.getSku().equals(request.getSku())) {
            validateSkuUnique(request.getSku(), variantId);
        }

        // Validate option values if changed
        if (request.getOptionValues() != null && !request.getOptionValues().isEmpty()) {
            validateOptionValues(variant.getProduct().getId(), request.getOptionValues());
            variant.setVariantName(generateVariantName(request.getOptionValues()));
            variant.setOptionValuesJson(convertMapToJson(request.getOptionValues()));
        }

        // Update fields
        variant.setSku(request.getSku());
        variant.setPrice(request.getPrice());
        variant.setCompareAtPrice(request.getCompareAtPrice());
        variant.setStockQuantity(request.getStockQuantity());
        variant.setLowStockThreshold(request.getLowStockThreshold());
        variant.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : variant.getDisplayOrder());
        variant.setIsActive(request.getIsActive() != null ? request.getIsActive() : variant.getIsActive());

        // Handle default variant logic
        if (Boolean.TRUE.equals(request.getIsDefault()) && !variant.getIsDefault()) {
            unsetCurrentDefaultVariant(variant.getProduct().getId());
            variant.setIsDefault(true);
            log.info("Updated variant ID: {} to be default", variantId);
        } else if (Boolean.FALSE.equals(request.getIsDefault()) && variant.getIsDefault()) {
            log.warn("Attempting to unset default variant without setting another as default - ignoring");
        }

        ProductVariant updatedVariant = variantRepository.save(variant);
        log.info("Successfully updated variant ID: {}", variantId);

        return variantMapper.toVariantResponse(updatedVariant);
    }

    @Override
    @Transactional
    public void deleteVariant(Long variantId) {
        log.info("Soft deleting variant ID: {}", variantId);

        ProductVariant variant = findVariantOrThrow(variantId);

        // Soft delete
        variant.softDelete();
        variantRepository.save(variant);

        // If this was the default variant, set another one as default
        if (variant.getIsDefault()) {
            List<ProductVariant> remainingVariants = variantRepository
                    .findByProductIdAndIsActiveTrueAndDeletedAtIsNullOrderByDisplayOrder(variant.getProduct().getId());

            if (!remainingVariants.isEmpty()) {
                ProductVariant newDefault = remainingVariants.get(0);
                newDefault.setIsDefault(true);
                variantRepository.save(newDefault);
                log.info("Set variant ID: {} as new default after deleting variant ID: {}",
                        newDefault.getId(), variantId);
            }
        }

        log.info("Successfully soft deleted variant ID: {}", variantId);
    }

    @Override
    @Transactional
    public ProductVariantResponse updateStock(Long variantId, Integer newStock) {
        log.info("Updating stock for variant ID: {} to {}", variantId, newStock);

        if (newStock < 0) {
            log.error("Invalid stock value: {} for variant ID: {}", newStock, variantId);
            throw new BadRequestException(messageService.getMessage("variant.stock.invalid"));
        }

        ProductVariant variant = findVariantOrThrow(variantId);
        variant.setStockQuantity(newStock);

        ProductVariant updatedVariant = variantRepository.save(variant);
        log.info("Successfully updated stock for variant ID: {}", variantId);

        return variantMapper.toVariantResponse(updatedVariant);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductWithVariantsResponse getProductWithVariants(Long productId) {
        log.info("Fetching complete product with variants for productId: {}", productId);

        // Validate product exists
        Product product = findProductOrThrow(productId);

        // Get variant options
        List<ProductVariantOption> options = variantOptionRepository.findByProductIdOrderByDisplayOrder(productId);

        // Get all variants
        List<ProductVariant> variants = variantRepository.findByProductIdAndDeletedAtIsNullOrderByDisplayOrder(productId);

        // Get default variant
        ProductVariant defaultVariant = variantRepository.findByProductIdAndIsDefaultTrueAndDeletedAtIsNull(productId)
                .orElse(null);

        // Compute statistics
        Integer totalVariants = variantRepository.countActiveVariantsByProductId(productId);
        BigDecimal minPrice = variantRepository.findMinPriceByProductId(productId);
        BigDecimal maxPrice = variantRepository.findMaxPriceByProductId(productId);
        Integer totalStock = variantRepository.sumStockByProductId(productId);
        Integer lowStockCount = (int) variantRepository.findLowStockVariants(productId).size();

        // Build response
        ProductWithVariantsResponse response = ProductWithVariantsResponse.builder()
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
                .totalVariants(totalVariants)
                .activeVariants(totalVariants)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .totalStock(totalStock != null ? totalStock : 0)
                .hasVariants(totalVariants > 0)
                .build();

        log.info("Successfully fetched product with {} variants for productId: {}", totalVariants, productId);
        return response;
    }

    //Finds a product by ID or throws ResourceNotFoundException
    private Product findProductOrThrow(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("Product not found with ID: {}", productId);
                    return new ResourceNotFoundException(
                            messageService.getMessage("product.not.found"));
                });
    }

    //Finds a variant by ID or throws ResourceNotFoundException
    private ProductVariant findVariantOrThrow(Long variantId) {
        return variantRepository.findById(variantId)
                .filter(v -> v.getDeletedAt() == null)
                .orElseThrow(() -> {
                    log.error("Variant not found with ID: {}", variantId);
                    return new ResourceNotFoundException(
                            messageService.getMessage("variant.not.found"));
                });
    }

    //Validates that SKU is unique across all variants
    private void validateSkuUnique(String sku, Long excludeId) {
        variantRepository.findBySkuAndDeletedAtIsNull(sku).ifPresent(existing -> {
            if (excludeId == null || !existing.getId().equals(excludeId)) {
                log.error("SKU already exists: {}", sku);
                throw new BadRequestException(
                        messageService.getMessage("variant.sku.already.exists"));
            }
        });
    }

    //Validates that option values match the product's defined variant options
    private void validateOptionValues(Long productId, Map<String, String> optionValues) {
        if (optionValues == null || optionValues.isEmpty()) {
            log.error("Option values cannot be empty for productId: {}", productId);
            throw new BadRequestException(
                    messageService.getMessage("variant.option.values.empty"));
        }

        List<ProductVariantOption> productOptions = variantOptionRepository
                .findByProductIdOrderByDisplayOrder(productId);

        if (productOptions.isEmpty()) {
            log.error("No variant options defined for productId: {}", productId);
            throw new BadRequestException(
                    messageService.getMessage("variant.options.not.defined"));
        }

        for (String optionName : optionValues.keySet()) {
            ProductVariantOption matchingOption = productOptions.stream()
                    .filter(opt -> opt.getOptionName().equals(optionName))
                    .findFirst()
                    .orElseThrow(() -> {
                        log.error("Invalid option name '{}' for productId: {}", optionName, productId);
                        return new BadRequestException(
                                messageService.getMessage("variant.option.name.invalid"));
                    });

            // Check option value is in allowed values
            String optionValue = optionValues.get(optionName);
            boolean valueExists = false;
            for (String allowedValue : matchingOption.getOptionValues()) {
                if (allowedValue.equals(optionValue)) {
                    valueExists = true;
                    break;
                }
            }

            if (!valueExists) {
                log.error("Invalid option value '{}' for option '{}' in productId: {}",
                        optionValue, optionName, productId);
                throw new BadRequestException(
                        messageService.getMessage("variant.option.value.invalid"));
            }
        }
    }

    //Generates a human-readable variant name from option values
    private String generateVariantName(Map<String, String> optionValues) {
        if (optionValues == null || optionValues.isEmpty()) {
            return "Default";
        }
        return String.join(" / ", optionValues.values());
    }

    //Unsets the current default variant for a product
    private void unsetCurrentDefaultVariant(Long productId) {
        variantRepository.findByProductIdAndIsDefaultTrueAndDeletedAtIsNull(productId)
                .ifPresent(currentDefault -> {
                    currentDefault.setIsDefault(false);
                    variantRepository.save(currentDefault);
                    log.info("Unset previous default variant ID: {} for productId: {}",
                            currentDefault.getId(), productId);
                });
    }

    //Converts option values map to JSON string for storage.
    private String convertMapToJson(Map<String, String> optionValues) {
        if (optionValues == null || optionValues.isEmpty()) {
            return "{}";
        }

        StringBuilder json = new StringBuilder("{");
        int i = 0;
        for (Map.Entry<String, String> entry : optionValues.entrySet()) {
            if (i > 0) {
                json.append(",");
            }
            json.append("\"").append(entry.getKey()).append("\":\"").append(entry.getValue()).append("\"");
            i++;
        }
        json.append("}");

        return json.toString();
    }
}
