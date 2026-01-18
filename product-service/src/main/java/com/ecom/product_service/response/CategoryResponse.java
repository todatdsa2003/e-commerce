package com.ecom.product_service.response;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {
    
    @Schema(example = "1")
    private Long id;
    
    @Schema(example = "Smartphones")
    private String name;
    
    @Schema(example = "smartphones")
    private String slug;
    
    @Schema(example = "7")
    private Long parentId;
    
    @Schema(example = "Electronics")
    private String parentName;
    
    private List<CategoryResponse> children;
    
    @Schema(example = "2026-01-18T10:30:00")
    private LocalDateTime createdAt;
    
    @Schema(example = "2026-01-18T15:45:00")
    private LocalDateTime updatedAt;
    
    @Schema(example = "false")
    private Boolean isDeleted;

}
