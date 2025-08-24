package com.deliverysystem.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "User login request")
public class LoginRequest {
    
    @NotBlank(message = "Username or email is required")
    @Schema(description = "Username or email", example = "john_doe")
    private String usernameOrEmail;
    
    @NotBlank(message = "Password is required")
    @Schema(description = "Password", example = "password123")
    private String password;
}
