package com.ecom.product_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductStatusRequest {
    
    @NotBlank(message = "Mã trạng thái không được để trống")
    private String code;
    
    @NotBlank(message = "Tên hiển thị không được để trống")
    private String label;
    
    private String description;
    
    private Integer displayOrder;
}
