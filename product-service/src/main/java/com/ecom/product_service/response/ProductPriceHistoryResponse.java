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
public class ProductPriceHistoryResponse {
    
    private Long id;
    private Long productId;
    private String productName;
    private BigDecimal oldPrice;
    private BigDecimal newPrice;
    private BigDecimal priceChange;
    private LocalDateTime changedAt;
}
