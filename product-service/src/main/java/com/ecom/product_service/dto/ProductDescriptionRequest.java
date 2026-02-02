package com.ecom.product_service.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductDescriptionRequest {

    @NotEmpty(message = "Keywords cannot be empty")
    private List<String> keywords;

    @NotNull(message = "Tone cannot be null")
    private DescriptionTone tone;

    private String additionalInfo;

    public enum DescriptionTone {
        PROFESSIONAL,
        CASUAL,
        MARKETING
    }
}
