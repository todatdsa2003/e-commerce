package com.ecom.product_service.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDescriptionResponse {
    
    private String generatedDescription;
    private List<String> alternativeDescriptions;  
    private String tone;
    private String message; 
}
