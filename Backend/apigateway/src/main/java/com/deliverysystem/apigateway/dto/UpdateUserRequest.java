package com.deliverysystem.apigateway.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "User update request")
public class UpdateUserRequest {
    
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Schema(description = "Username", example = "john_doe_updated")
    private String username;
    
    @Email(message = "Invalid email format")
    @Schema(description = "Email address", example = "john.updated@example.com")
    private String email;
    
    @Schema(description = "First name", example = "John")
    private String firstName;
    
    @Schema(description = "Last name", example = "Doe")
    private String lastName;
    
    @Schema(description = "Phone number", example = "+1234567890")
    private String phoneNumber;
    
    @Schema(description = "Restaurant name (for restaurant owners)", example = "John's Updated Restaurant")
    private String restaurantName;
    
    @Schema(description = "User address", example = "456 New St, City")
    private String address;
}
