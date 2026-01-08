package com.ecom.user_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProfileRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    @Pattern(
        regexp = "^[a-zA-Z0-9\\s\\u00C0-\\u024F\\u1E00-\\u1EFF._'-]+$",
        message = "Full name contains invalid characters. Only letters, numbers, spaces and common punctuation allowed"
    )
    private String fullName;

    @Pattern(
        regexp = "^(\\+84|0)[0-9]{9}$",
        message = "Phone number must be valid Vietnamese format (e.g., 0912345678 or +84912345678)"
    )
    private String phoneNumber;
}
