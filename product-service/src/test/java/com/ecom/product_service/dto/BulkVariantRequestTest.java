package com.ecom.product_service.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

class BulkVariantRequestTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // Happy path
    @Test
    void shouldPassWhenRequestIsValid() {
        BulkVariantRequest request = new BulkVariantRequest();
        request.setOptions(validOptions(2));
        request.setVariants(validVariants(10));

        Set<ConstraintViolation<BulkVariantRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    // Options
    @Test
    void shouldFailWhenOptionsIsNull() {
        BulkVariantRequest request = new BulkVariantRequest();
        request.setOptions(null);
        request.setVariants(validVariants(1));

        Set<ConstraintViolation<BulkVariantRequest>> violations = validator.validate(request);

        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("options-required")));
    }

    @Test
    void shouldFailWhenOptionsIsEmpty() {
        BulkVariantRequest request = new BulkVariantRequest();
        request.setOptions(List.of());
        request.setVariants(validVariants(1));

        Set<ConstraintViolation<BulkVariantRequest>> violations = validator.validate(request);

        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("options-required")));
    }

    @Test
    void shouldFailWhenOptionsExceedMaxSize() {
        BulkVariantRequest request = new BulkVariantRequest();
        request.setOptions(validOptions(6));
        request.setVariants(validVariants(1));

        Set<ConstraintViolation<BulkVariantRequest>> violations = validator.validate(request);

        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("options-size-invalid")));
    }

    // Variants
    @Test
    void shouldFailWhenVariantsIsNull() {
        BulkVariantRequest request = new BulkVariantRequest();
        request.setOptions(validOptions(1));
        request.setVariants(null);

        Set<ConstraintViolation<BulkVariantRequest>> violations = validator.validate(request);

        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("variants-required")));
    }

    @Test
    void shouldFailWhenVariantsIsEmpty() {
        BulkVariantRequest request = new BulkVariantRequest();
        request.setOptions(validOptions(1));
        request.setVariants(List.of());

        Set<ConstraintViolation<BulkVariantRequest>> violations = validator.validate(request);

        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("variants-required")));
    }

    @Test
    void shouldFailWhenVariantsExceedMaxSize() {
        BulkVariantRequest request = new BulkVariantRequest();
        request.setOptions(validOptions(1));
        request.setVariants(validVariants(101));

        Set<ConstraintViolation<BulkVariantRequest>> violations = validator.validate(request);

        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("variants-size-invalid")));
    }

    // Cascade
    @Test
    void shouldFailWhenOptionItemIsInvalid() {
        BulkVariantRequest request = new BulkVariantRequest();
        request.setOptions(List.of(new ProductVariantOptionRequest()));
        request.setVariants(validVariants(1));

        Set<ConstraintViolation<BulkVariantRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFailWhenVariantItemIsInvalid() {
        BulkVariantRequest request = new BulkVariantRequest();
        request.setOptions(validOptions(1));
        request.setVariants(List.of(new ProductVariantRequest()));

        Set<ConstraintViolation<BulkVariantRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    // Helpers
    private List<ProductVariantOptionRequest> validOptions(int size) {
        List<ProductVariantOptionRequest> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            ProductVariantOptionRequest option = new ProductVariantOptionRequest();
            option.setOptionName("Color");
            option.setOptionValues(List.of("Red", "Black"));
            option.setDisplayOrder(0);
            list.add(option);
        }
        return list;
    }

    private List<ProductVariantRequest> validVariants(int size) {
        List<ProductVariantRequest> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            ProductVariantRequest variant = new ProductVariantRequest();
            variant.setSku("SKU-" + i);
            variant.setPrice(new BigDecimal("100.00"));
            variant.setCompareAtPrice(new BigDecimal("120.00"));
            variant.setStockQuantity(10);
            variant.setLowStockThreshold(5);
            variant.setOptionValues(Map.of("size", "M"));
            variant.setIsActive(true);
            variant.setDisplayOrder(0);
            list.add(variant);
        }
        return list;
    }
}
