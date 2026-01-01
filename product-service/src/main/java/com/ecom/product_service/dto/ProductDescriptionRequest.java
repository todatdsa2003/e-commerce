package com.ecom.product_service.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductDescriptionRequest {
    
    @NotEmpty(message = "Keywords không được để trống")
    private List<String> keywords;
    
    @NotNull(message = "Tone không được để trống")
    private DescriptionTone tone;
    
    private String additionalInfo; // Thông tin thêm (optional)
    
    public enum DescriptionTone {
        PROFESSIONAL,  // Chuyên nghiệp
        CASUAL,        // Thân thiện, gần gũi
        MARKETING      // Bán hàng, hấp dẫn
    }
}
