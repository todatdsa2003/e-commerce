package com.ecom.product_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Custom product attribute")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductAttributeRequest {
    
    @Schema(description = "Attribute ID for updating existing attribute")
    private Long id; 
    
    @Schema(description = "Name of the attribute", example = "Color", required = true)
    @NotBlank(message = "{validation.attribute.name.required}")
    private String attributeName;
    
    @Schema(description = "Value of the attribute", example = "Space Black", required = true)
    @NotBlank(message = "{validation.attribute.value.required}")
    private String attributeValue;
}
