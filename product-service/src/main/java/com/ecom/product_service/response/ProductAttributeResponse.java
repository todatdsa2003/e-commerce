package com.ecom.product_service.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductAttributeResponse {
    
    private Long id;
    private String attributeName;
    private String attributeValue;
}
