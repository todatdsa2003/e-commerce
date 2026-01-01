package com.ecom.product_service.dto;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

class ProductVariantOptionRequestTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // Happy path
    @Test
    void shouldPassWhenRequestIsValid() {
        ProductVariantOptionRequest request = validRequest();

        Set<ConstraintViolation<ProductVariantOptionRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    // Option name
    @Test
    void shouldFailWhenOptionNameIsBlank() {
        ProductVariantOptionRequest request = validRequest();
        request.setOptionName(" ");

        Set<ConstraintViolation<ProductVariantOptionRequest>> violations = validator.validate(request);

        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("name-required")));
    }

    @Test
    void shouldFailWhenOptionNameTooLong() {
        ProductVariantOptionRequest request = validRequest();
        request.setOptionName("A".repeat(51));

        Set<ConstraintViolation<ProductVariantOptionRequest>> violations = validator.validate(request);

        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("name-too-long")));
    }

    // Option values
    @Test
    void shouldFailWhenOptionValuesIsEmpty() {
        ProductVariantOptionRequest request = validRequest();
        request.setOptionValues(List.of());

        Set<ConstraintViolation<ProductVariantOptionRequest>> violations = validator.validate(request);

        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("values-required")));
    }

    @Test
    void shouldFailWhenOptionValuesExceedMaxSize() {
        ProductVariantOptionRequest request = validRequest();
        request.setOptionValues(
                IntStream.range(0, 21)
                        .mapToObj(i -> "Value-" + i)
                        .toList());

        Set<ConstraintViolation<ProductVariantOptionRequest>> violations = validator.validate(request);

        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("values-invalid")));
    }

    @Test
    void shouldFailWhenOptionValueIsBlank() {
        ProductVariantOptionRequest request = validRequest();
        request.setOptionValues(List.of(""));

        Set<ConstraintViolation<ProductVariantOptionRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    // Display order
    @Test
    void shouldFailWhenDisplayOrderIsNegative() {
        ProductVariantOptionRequest request = validRequest();
        request.setDisplayOrder(-1);

        Set<ConstraintViolation<ProductVariantOptionRequest>> violations = validator.validate(request);

        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("display-order-invalid")));
    }

    // Helper
    private ProductVariantOptionRequest validRequest() {
        ProductVariantOptionRequest request = new ProductVariantOptionRequest();
        request.setOptionName("Color");
        request.setOptionValues(List.of("Red", "Black"));
        request.setDisplayOrder(0);
        return request;
    }
}
