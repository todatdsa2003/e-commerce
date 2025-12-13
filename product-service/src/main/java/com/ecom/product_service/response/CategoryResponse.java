package com.ecom.product_service.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {
    
    private Long id;
    private String name;
    private String slug;
    private Long parentId;
    private String parentName;
    private List<CategoryResponse> children;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
