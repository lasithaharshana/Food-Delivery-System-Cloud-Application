package com.deliverysystem.apigateway.controller;

import com.deliverysystem.apigateway.dto.AuthRequest;
import com.deliverysystem.apigateway.dto.AuthResponse;
import com.deliverysystem.apigateway.dto.RegisterRequest;
import com.deliverysystem.apigateway.dto.ValidateTokenRequest;
import com.deliverysystem.apigateway.service.AuthServiceClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Authentication", description = "Authentication and user registration endpoints")
public class AuthController {
    
    @Autowired
    private AuthServiceClient authServiceClient;
    
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user with username/email and password")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful",
                content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid credentials"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<String> login(@Valid @RequestBody AuthRequest authRequest) {
        try {
            // Return the exact response from auth service
            return authServiceClient.login(authRequest);
                    
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Authentication service error: " + e.getMessage() + "\"}");
        }
    }
    
    @PostMapping("/register")
    @Operation(summary = "User registration", description = "Register a new user in the system")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User registered successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "User already exists"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            // Validate role-specific requirements
            if ("RESTAURANT".equals(registerRequest.getRole()) && 
                (registerRequest.getRestaurantName() == null || registerRequest.getRestaurantName().trim().isEmpty())) {
                return ResponseEntity.badRequest()
                        .body("{\"error\": \"Restaurant name is required for restaurant registration\"}");
            }
            
            // Return the exact response from auth service
            return authServiceClient.register(registerRequest);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Registration service error: " + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/validate")
    @Operation(summary = "Validate JWT token", description = "Validate the provided JWT token from request body")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Token is valid"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid or expired token")
    })
    public ResponseEntity<String> validateToken(@Valid @RequestBody ValidateTokenRequest request) {
        try {
            String token = request.getToken();
            
            if (token != null && !token.trim().isEmpty()) {
                // Use auth service for token validation
                return authServiceClient.validateToken(token);
            }
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("{\"error\": \"Missing or invalid token\"}");
                    
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("{\"error\": \"Token validation failed: " + e.getMessage() + "\"}");
        }
    }
}
