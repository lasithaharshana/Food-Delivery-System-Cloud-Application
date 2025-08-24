package com.deliverysystem.apigateway.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class AuthRequest {
    @JsonProperty("usernameOrEmail")
    private String usernameOrEmail;
    
    // For backward compatibility
    private String username;
    private String email;
    
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
