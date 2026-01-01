package com.ecom.product_service.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BulkVariantRequest {

    @NotEmpty(message = "{error.variant.options-required}")
    @Size(min = 1, max = 5, message = "{error.variant.options-size-invalid}")
    @Valid
    private List<ProductVariantOptionRequest> options;

    @NotEmpty(message = "{error.variant.variants-required}")
    @Size(min = 1, max = 100, message = "{error.variant.variants-size-invalid}")
    @Valid
    private List<ProductVariantRequest> variants;
}
