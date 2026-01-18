package com.ecom.product_service.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "Request object for defining variant option (e.g., Size, Color) and its possible values")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantOptionRequest {

    @Schema(description = "Name of the variant option", example = "Size", required = true)
    @NotBlank(message = "{error.variant-option.name-required}")
    @Size(max = 50, message = "{error.variant-option.name-too-long}")
    private String optionName;

    @Schema(description = "List of possible values for this option", example = "[\"128GB\", \"256GB\", \"512GB\", \"1TB\"]", required = true)
    @NotEmpty(message = "{error.variant-option.values-required}")
    @Size(min = 1, max = 20, message = "{error.variant-option.values-invalid}")
    private List<@NotBlank(message = "{error.variant-option.value-blank}") String> optionValues;

    @Schema(description = "Display order for sorting options", example = "0")
    @Min(value = 0, message = "{error.variant-option.display-order-invalid}")
    private Integer displayOrder = 0;
}
