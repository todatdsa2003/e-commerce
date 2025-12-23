package com.ecom.product_service.dto;

import java.math.BigDecimal;
import java.util.Map;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantRequest {

    @NotBlank(message = "{error.variant.sku-required}")
    @Size(max = 100, message = "{error.variant.sku-too-long}")
    @Pattern(regexp = "^[A-Z0-9-]+$", message = "{error.variant.sku-invalid-format}")
    private String sku;

    @Size(max = 200, message = "{error.variant.name-too-long}")
    private String variantName;

    @NotNull(message = "{error.variant.price-required}")
    @DecimalMin(value = "0.01", message = "{error.variant.price-invalid}")
    @Digits(integer = 10, fraction = 2, message = "{error.variant.price-format-invalid}")
    private BigDecimal price;

    @DecimalMin(value = "0.01", message = "{error.variant.compare-price-invalid}")
    @Digits(integer = 10, fraction = 2, message = "{error.variant.price-format-invalid}")
    private BigDecimal compareAtPrice;

    @NotNull(message = "{error.variant.stock-required}")
    @Min(value = 0, message = "{error.variant.stock-invalid}")
    private Integer stockQuantity;

    @Min(value = 0, message = "{error.variant.low-stock-threshold-invalid}")
    private Integer lowStockThreshold = 5;

    @NotEmpty(message = "{error.variant.options-required}")
    private Map<@NotBlank(message = "{error.variant.option-key-blank}") String, 
                @NotBlank(message = "{error.variant.option-value-blank}") String> optionValues;

    private Boolean isDefault = false;

    private Boolean isActive = true;

    @Min(value = 0, message = "{error.variant.display-order-invalid}")
    private Integer displayOrder = 0;

    @AssertTrue(message = "{error.variant.compare-price-must-greater}")
    public boolean isComparePriceValid() {
        if (compareAtPrice == null) {
            return true;
        }
        if (price == null) {
            return true;
        }
        return compareAtPrice.compareTo(price) > 0;
    }
}
