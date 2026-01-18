package com.ecom.product_service.response;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    
    @Schema(example = "404")
    private int status;
    
    @Schema(example = "Brand not found with id: 999")
    private String message;
    
    @Schema(example = "2026-01-18T15:45:00")
    private LocalDateTime timestamp;
    
    private Map<String, String> errors;
    

    public ErrorResponse(int status, String message, LocalDateTime timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }
}
