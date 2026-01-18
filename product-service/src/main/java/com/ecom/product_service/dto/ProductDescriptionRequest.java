package com.ecom.product_service.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "Request object for AI-powered product description generation")
@Data
public class ProductDescriptionRequest {
    
    @Schema(description = "List of keywords describing the product", example = "[\"premium\", \"wireless\", \"noise-cancelling\", \"headphones\"]", required = true)
    @NotEmpty(message = "Keywords cannot be empty")
    private List<String> keywords;
    
    @Schema(description = "Tone of the generated description", example = "PROFESSIONAL", required = true)
    @NotNull(message = "Tone cannot be null")
    private DescriptionTone tone;
    
    @Schema(description = "Additional context or specific details to include", example = "Include information about battery life and comfort")
    private String additionalInfo;
    
    public enum DescriptionTone {
        @Schema(description = "Professional and formal tone")
        PROFESSIONAL,
        @Schema(description = "Friendly and casual tone")
        CASUAL,
        @Schema(description = "Marketing-oriented and persuasive tone")
        MARKETING
    }
}
