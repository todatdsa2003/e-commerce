package com.ecom.product_service.dto;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
public class ProductVariantOptionRequest {

    @NotBlank(message = "{error.variant-option.name-required}")
    @Size(max = 50, message = "{error.variant-option.name-too-long}")
    private String optionName;

    @NotEmpty(message = "{error.variant-option.values-required}")
    @Size(min = 1, max = 20, message = "{error.variant-option.values-invalid}")
    private List<@NotBlank(message = "{error.variant-option.value-blank}") String> optionValues;

    @Min(value = 0, message = "{error.variant-option.display-order-invalid}")
    private Integer displayOrder = 0;
}
