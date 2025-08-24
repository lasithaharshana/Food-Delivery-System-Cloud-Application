package com.deliverysystem.apigateway.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Token validation request")
public class ValidateTokenRequest {

    @NotBlank(message = "Token is required")
    @Schema(description = "JWT token to validate", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;
}
