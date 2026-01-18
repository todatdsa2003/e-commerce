package com.ecom.product_service.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Request object for creating or updating a product")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @Schema(description = "Product name", example = "iPhone 15 Pro Max", required = true)
    @NotBlank(message = "{validation.name.required}")
    private String name;

    @Schema(description = "Detailed product description", example = "The latest flagship smartphone with A17 Pro chip and titanium design")
    private String description;

    @Schema(description = "Product base price", example = "1199.99", required = true)
    @NotNull(message = "{validation.price.required}")
    @Positive(message = "{validation.price.positive}")
    @Digits(integer = 10, fraction = 2, message = "{validation.price.digits}")
    private BigDecimal price;

    @Schema(description = "Available quantity in stock", example = "100", required = true)
    @NotNull(message = "{validation.availability.required}")
    @Min(value = 0, message = "{validation.availability.min}")
    private Integer availability;

    @Schema(description = "Product status ID (e.g., active, inactive, discontinued)", example = "1", required = true)
    @NotNull(message = "{validation.status.required}")
    private Long statusId;

    @Schema(description = "Product category ID", example = "3", required = true)
    @NotNull(message = "{validation.category.required}")
    private Long categoryId;

    @Schema(description = "Brand ID associated with the product", example = "2")
    private Long brandId;

    @Schema(description = "List of custom product attributes (e.g., color, size, material)")
    @Valid
    private List<ProductAttributeRequest> attributes = new ArrayList<>();
}
