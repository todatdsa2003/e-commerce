package com.ecom.product_service.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductPriceHistoryRequest {

    private Long productId;
    private Long variantId;

    @NotNull(message = "{error.price-history.new-price-required}")
    @DecimalMin(value = "0.01", message = "{error.price-history.new-price-invalid}")
    @Digits(integer = 10, fraction = 2, message = "{error.price-history.new-price-format-invalid}")
    private BigDecimal newPrice;

    private String changeReason;
    private String changedBy;

    @AssertTrue(message = "{error.price-history.product-or-variant-required}")
    public boolean isProductOrVariantProvided() {
        return (productId != null && variantId == null) || (productId == null && variantId != null);
    }
}
