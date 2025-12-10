package com.ecom.product_service.responses;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandResponse {
    
    private Long id;
    private String name;
    private Long productCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
