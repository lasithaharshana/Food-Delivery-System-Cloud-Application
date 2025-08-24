package com.deliverysystem.apigateway.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Authentication request")
public class AuthRequest {
    @JsonProperty("usernameOrEmail")
    @Schema(description = "Username or email address", example = "john_doe or john@example.com")
    private String usernameOrEmail;
    
    // For backward compatibility
    @Schema(description = "Username (for backward compatibility)", example = "john_doe")
    private String username;
    
    @Schema(description = "Email (for backward compatibility)", example = "john@example.com")
    private String email;
    
    @Schema(description = "User password", example = "password123")
    private String password;
    
    // Method to get the appropriate username/email value for auth service
    public String getUsernameOrEmail() {
        if (usernameOrEmail != null && !usernameOrEmail.trim().isEmpty()) {
            return usernameOrEmail;
        }
        if (username != null && !username.trim().isEmpty()) {
            return username;
        }
        if (email != null && !email.trim().isEmpty()) {
            return email;
        }
        return usernameOrEmail;
    }
}
