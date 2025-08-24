package com.deliverysystem.authservice.dto;

import com.deliverysystem.authservice.entity.User;
import com.deliverysystem.authservice.validation.RoleBasedValidation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RoleBasedValidation
@Schema(description = "User registration request")
public class RegisterRequest {
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Schema(description = "Username", example = "john_doe")
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(description = "Email address", example = "john.doe@example.com")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Schema(description = "Password", example = "password123")
    private String password;
    
    @NotBlank(message = "First name is required")
    @Schema(description = "First name", example = "John")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Schema(description = "Last name", example = "Doe")
    private String lastName;
    
    @Schema(description = "Phone number", example = "+1234567890")
    private String phoneNumber;
    
    @NotNull(message = "Role is required")
    @Schema(description = "User role - CUSTOMER or RESTAURANT", example = "RESTAURANT", allowableValues = {"CUSTOMER", "RESTAURANT"})
    private User.Role role;
    
    @Schema(description = "User address", example = "123 Main St, City")
    private String address;
    
    // Additional fields for restaurant owners
    @Schema(description = "Restaurant name (required only for RESTAURANT role)", example = "John's Restaurant")
    private String restaurantName;
}
