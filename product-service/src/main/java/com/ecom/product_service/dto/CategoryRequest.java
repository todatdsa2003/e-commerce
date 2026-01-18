package com.ecom.product_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {
    
    @Schema(example = "Smartphones")
    @NotBlank(message = "{validation.category.name.required}")
    private String name;
    
    @Schema(example = "1")
    private Long parentId;
}
