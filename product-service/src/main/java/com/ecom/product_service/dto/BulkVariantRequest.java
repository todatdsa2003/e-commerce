package com.ecom.product_service.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "Request object for creating multiple product variants in bulk along with their options")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BulkVariantRequest {

    @Schema(description = "List of variant options (e.g., Size, Color) with their possible values", required = true)
    @NotEmpty(message = "{error.variant.options-required}")
    @Size(min = 1, max = 5, message = "{error.variant.options-size-invalid}")
    @Valid
    private List<ProductVariantOptionRequest> options;

    @Schema(description = "List of variants to create with their specific configurations", required = true)
    @NotEmpty(message = "{error.variant.variants-required}")
    @Size(min = 1, max = 100, message = "{error.variant.variants-size-invalid}")
    @Valid
    private List<ProductVariantRequest> variants;
}
