package com.ecom.product_service.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private Boolean isDeleted;

    private String thumbnailUrl;

    @Builder.Default
    private List<ProductImageResponse> images = new ArrayList<>();

    @Builder.Default
    private List<ProductAttributeResponse> attributes = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
