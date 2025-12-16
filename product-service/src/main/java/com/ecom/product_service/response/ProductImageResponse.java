package com.ecom.product_service.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageResponse {
    
    private Long id;
    private Long productId;
    private String imageUrl;
    private Boolean isThumbnail;
    private LocalDateTime createdAt;
}
