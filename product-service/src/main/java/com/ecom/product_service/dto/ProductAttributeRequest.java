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
    
    @NotBlank(message = "Tên thuộc tính không được để trống")
    private String attributeName;
    
    @NotBlank(message = "Giá trị thuộc tính không được để trống")
    private String attributeValue;
}
