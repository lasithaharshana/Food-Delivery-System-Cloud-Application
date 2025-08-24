package com.deliverysystem.apigateway.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data
public class RegisterRequest {
    @NotBlank
    private String username;
    
    @Email
    @NotBlank
    private String email;
    
    @NotBlank
    private String password;
    
    @NotBlank
    private String firstName;
    
    @NotBlank
    private String lastName;
    
    private String phoneNumber;
    
    @NotBlank
    private String role; // CUSTOMER or RESTAURANT
    
    private String restaurantName; // Required for RESTAURANT role
    
    @NotBlank
    private String address;
}
