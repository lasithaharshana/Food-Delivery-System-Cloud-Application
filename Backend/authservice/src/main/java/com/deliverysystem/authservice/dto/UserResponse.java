package com.deliverysystem.authservice.dto;

import com.deliverysystem.authservice.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "User response")
public class UserResponse {
    
    @Schema(description = "User ID", example = "1")
    private Long id;
    
    @Schema(description = "Username", example = "john_doe")
    private String username;
    
    @Schema(description = "Email address", example = "john.doe@example.com")
    private String email;
    
    @Schema(description = "First name", example = "John")
    private String firstName;
    
    @Schema(description = "Last name", example = "Doe")
    private String lastName;
    
    @Schema(description = "Phone number", example = "+1234567890")
    private String phoneNumber;
    
    @Schema(description = "User role", example = "CUSTOMER")
    private User.Role role;
    
    @Schema(description = "Account status", example = "true")
    private Boolean isActive;
    
    @Schema(description = "Account creation timestamp")
    private LocalDateTime createdAt;
    
    @Schema(description = "Account last update timestamp")
    private LocalDateTime updatedAt;
    
    @Schema(description = "Restaurant name (for restaurant owners)")
    private String restaurantName;
    
    @Schema(description = "User address")
    private String address;
    
    @Schema(description = "Profile image URL", example = "https://example.com/profile.jpg")
    private String imageUrl;
}
