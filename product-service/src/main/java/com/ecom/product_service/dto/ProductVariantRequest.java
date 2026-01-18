package com.ecom.product_service.dto;

import java.math.BigDecimal;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
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

@Schema(description = "Request object for creating or updating a product variant")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantRequest {

    @Schema(description = "Stock Keeping Unit - unique identifier for inventory management", example = "IPH15-PM-256-BLK", required = true)
    @NotBlank(message = "{error.variant.sku-required}")
    @Size(max = 100, message = "{error.variant.sku-too-long}")
    @Pattern(regexp = "^[A-Z0-9-]+$", message = "{error.variant.sku-invalid-format}")
    private String sku;

    @Schema(description = "Display name for this variant", example = "iPhone 15 Pro Max - 256GB - Black")
    @Size(max = 200, message = "{error.variant.name-too-long}")
    private String variantName;

    @Schema(description = "Selling price for this variant", example = "1199.99", required = true)
    @NotNull(message = "{error.variant.price-required}")
    @DecimalMin(value = "0.01", message = "{error.variant.price-invalid}")
    @Digits(integer = 10, fraction = 2, message = "{error.variant.price-format-invalid}")
    private BigDecimal price;

    @Schema(description = "Original price before discount (must be greater than price)", example = "1299.99")
    @DecimalMin(value = "0.01", message = "{error.variant.compare-price-invalid}")
    @Digits(integer = 10, fraction = 2, message = "{error.variant.price-format-invalid}")
    private BigDecimal compareAtPrice;

    @Schema(description = "Available stock quantity", example = "50", required = true)
    @NotNull(message = "{error.variant.stock-required}")
    @Min(value = 0, message = "{error.variant.stock-invalid}")
    private Integer stockQuantity;

    @Schema(description = "Threshold for low stock warnings", example = "5")
    @Min(value = 0, message = "{error.variant.low-stock-threshold-invalid}")
    private Integer lowStockThreshold = 5;

    @Schema(description = "Map of option names to values (e.g., {\"Size\": \"Large\", \"Color\": \"Black\"})", example = "{\"Size\": \"256GB\", \"Color\": \"Space Black\"}", required = true)
    @NotEmpty(message = "{error.variant.options-required}")
    private Map<@NotBlank(message = "{error.variant.option-key-blank}") String, 
                @NotBlank(message = "{error.variant.option-value-blank}") String> optionValues;

    @Schema(description = "Whether this is the default variant for the product", example = "false")
    private Boolean isDefault = false;

    @Schema(description = "Whether this variant is active and available for purchase", example = "true")
    private Boolean isActive = true;

    @Schema(description = "Display order for sorting variants", example = "0")
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
