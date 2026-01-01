package com.ecom.product_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageRequest {
    
    @NotBlank(message = "URL ảnh không được để trống")
    private String imageUrl;

    @NotNull(message = "Vui lòng chọn ảnh chính")
    private Boolean isThumbnail;
}
