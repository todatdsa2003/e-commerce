package com.ecom.product_service.dto;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;

public class ProductVariantRequestTest {
    private final jakarta.validation.Validator validator = jakarta.validation.Validation
            .buildDefaultValidatorFactory()
            .getValidator();
    @Test
    void testSkuValidation() {
        ProductVariantRequest request = new ProductVariantRequest();
        request.setSku("invalid sku"); 

        Set<ConstraintViolation<ProductVariantRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("sku-invalid-format")));
    }

    @Test
    void testComparePriceValidation() {
        ProductVariantRequest request = new ProductVariantRequest();
        request.setPrice(new BigDecimal("100"));
        request.setCompareAtPrice(new BigDecimal("80"));

        Set<ConstraintViolation<ProductVariantRequest>> violations = validator.validate(request);

        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("compare-price-must-greater")));
    }
}
