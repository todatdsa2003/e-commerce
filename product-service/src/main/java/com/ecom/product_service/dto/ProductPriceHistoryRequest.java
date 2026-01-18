package com.ecom.product_service.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Request object for recording product or variant price changes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductPriceHistoryRequest {
    
    @Schema(description = "Product ID for product-level price change. Either productId or variantId must be provided, not both.", example = "1")
    private Long productId;
    
    @Schema(description = "Variant ID for variant-level price change. Either productId or variantId must be provided, not both.", example = "10")
    private Long variantId;
    
    @Schema(description = "New price value", example = "899.99", required = true)
    @NotNull(message = "{error.price-history.new-price-required}")
    @DecimalMin(value = "0.01", message = "{error.price-history.new-price-invalid}")
    @Digits(integer = 10, fraction = 2, message = "{error.price-history.new-price-format-invalid}")
    private BigDecimal newPrice;
    
    @Schema(description = "Reason for the price change", example = "Seasonal discount campaign")
    private String changeReason;
    
    @Schema(description = "Person who initiated the price change", example = "admin@example.com")
    private String changedBy;
    
    @AssertTrue(message = "{error.price-history.product-or-variant-required}")
    public boolean isProductOrVariantProvided() {
        return (productId != null && variantId == null) || (productId == null && variantId != null);
    }
}
