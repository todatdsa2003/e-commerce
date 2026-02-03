package com.ecom.product_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductListDTO {

    private Long id;
    private String name;
    private String slug;
    private String description;
    private BigDecimal price;
    private Integer availability;

    // Status info
    private Long statusId;
    private String statusName;

    // Category info
    private Long categoryId;
    private String categoryName;

    // Brand info
    private Long brandId;
    private String brandName;

    private Boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    //Constructor for JPQL projection
    public ProductListDTO(
            Long id,
            String name,
            String slug,
            String description,
            BigDecimal price,
            Integer availability,
            Long statusId,
            String statusName,
            Long categoryId,
            String categoryName,
            Long brandId,
            String brandName,
            Boolean isDeleted,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.price = price;
        this.availability = availability;
        this.statusId = statusId;
        this.statusName = statusName;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.brandId = brandId;
        this.brandName = brandName;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
