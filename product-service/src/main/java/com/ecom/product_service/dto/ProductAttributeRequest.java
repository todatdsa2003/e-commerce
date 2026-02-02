package com.ecom.product_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductAttributeRequest {

    private Long id;

    @NotBlank(message = "{validation.attribute.name.required}")
    private String attributeName;

    @NotBlank(message = "{validation.attribute.value.required}")
    private String attributeValue;
}
