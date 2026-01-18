package com.ecom.product_service.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandResponse {
    
    @Schema(example = "1")
    private Long id;
    
    @Schema(example = "Apple")
    private String name;
    
    @Schema(example = "25")
    private Long productCount;
    
    @Schema(example = "2026-01-18T10:30:00")
    private LocalDateTime createdAt;
    
    @Schema(example = "2026-01-18T15:45:00")
    private LocalDateTime updatedAt;
    
    @Schema(example = "false")
    private Boolean isDeleted;
}
