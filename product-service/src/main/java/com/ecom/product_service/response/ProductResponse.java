package com.ecom.product_service.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    
    private Long id;
    private String name;
    private String slug;
    private String description;
    private BigDecimal price;
    private Integer availability;
    private Long statusId;
    private String statusName;
    private Long categoryId;
    private String categoryName;
    private Long brandId;
    private String brandName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
