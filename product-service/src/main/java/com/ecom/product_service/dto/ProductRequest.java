package com.ecom.product_service.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @NotBlank(message = "{validation.name.required}")
    private String name;

    private String description;

    @NotNull(message = "{validation.price.required}")
    @Positive(message = "{validation.price.positive}")
    @Digits(integer = 10, fraction = 2, message = "{validation.price.digits}")
    private BigDecimal price;

    @NotNull(message = "{validation.availability.required}")
    @Min(value = 0, message = "{validation.availability.min}")
    private Integer availability;

    @NotNull(message = "{validation.status.required}")
    private Long statusId;

    @NotNull(message = "{validation.category.required}")
    private Long categoryId;

    private Long brandId;

    @Valid
    private List<ProductAttributeRequest> attributes = new ArrayList<>();
}
