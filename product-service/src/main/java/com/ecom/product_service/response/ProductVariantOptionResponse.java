package com.ecom.product_service.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariantOptionResponse {

    private Long id;
    private String optionName;
    private List<String> optionValues;
    private Integer displayOrder;
}
