package com.deliverysystem.apigateway.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Authentication response")
public class AuthResponse {
    @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
    
    @Schema(description = "Token type", example = "Bearer")
    private String type = "Bearer";
    
    @Schema(description = "Username", example = "john_doe")
    private String username;
    
    @Schema(description = "User role", example = "CUSTOMER")
    private String role;
    
    @Schema(description = "User ID", example = "1")
    private Long id;
    
    public AuthResponse(String token, String username, String role, Long id) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.id = id;
    }
}
