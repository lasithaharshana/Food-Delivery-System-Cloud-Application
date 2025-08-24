package com.deliverysystem.apigateway.controller;

import com.deliverysystem.apigateway.dto.ApiResponse;
import com.deliverysystem.apigateway.dto.AuthRequest;
import com.deliverysystem.apigateway.dto.AuthResponse;
import com.deliverysystem.apigateway.dto.RegisterRequest;
import com.deliverysystem.apigateway.service.AuthServiceClient;
import com.deliverysystem.apigateway.util.JwtUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
    
    @Autowired
    private JwtUtil jwtUtil;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user with username/email and password")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful",
                content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid credentials"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest authRequest) {
        try {
            ResponseEntity<String> response = authServiceClient.login(authRequest);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                // Parse the response from auth service
                JsonNode jsonResponse = objectMapper.readTree(response.getBody());
                
                // Auth service returns: { "accessToken": "...", "tokenType": "Bearer", "user": {...} }
                if (jsonResponse.has("accessToken") && jsonResponse.has("user")) {
                    String token = jsonResponse.get("accessToken").asText();
                    JsonNode userData = jsonResponse.get("user");
                    
                    String username = userData.get("username").asText();
                    String role = userData.get("role").asText();
                    Long userId = userData.get("id").asLong();
                    
                    // Return the token from auth service directly
                    AuthResponse authResponse = new AuthResponse(token, username, role, userId);
                    return ResponseEntity.ok(ApiResponse.success(authResponse));
                }
            }
            
            return ResponseEntity.status(response.getStatusCode())
                    .body(ApiResponse.error("Authentication failed"));
                    
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Authentication service error: " + e.getMessage()));
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
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            // Validate role-specific requirements
            if ("RESTAURANT".equals(registerRequest.getRole()) && 
                (registerRequest.getRestaurantName() == null || registerRequest.getRestaurantName().trim().isEmpty())) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Restaurant name is required for restaurant registration"));
            }
            
            ResponseEntity<String> response = authServiceClient.register(registerRequest);
            
            if (response.getStatusCode() == HttpStatus.CREATED || response.getStatusCode() == HttpStatus.OK) {
                return ResponseEntity.status(response.getStatusCode())
                        .body(ApiResponse.success("User registered successfully"));
            } else {
                return ResponseEntity.status(response.getStatusCode())
                        .body(ApiResponse.error("Registration failed"));
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Registration service error: " + e.getMessage()));
        }
    }
    
    @GetMapping("/validate")
    @Operation(summary = "Validate JWT token", description = "Validate the provided JWT token")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Token is valid"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid or expired token")
    })
    public ResponseEntity<ApiResponse<String>> validateToken(
            @Parameter(description = "JWT token in Authorization header") @RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                
                if (jwtUtil.validateToken(token) && !jwtUtil.isTokenExpired(token)) {
                    String username = jwtUtil.getUsernameFromToken(token);
                    String role = jwtUtil.getRoleFromToken(token);
                    
                    return ResponseEntity.ok(ApiResponse.success("Token is valid for user: " + username + " with role: " + role));
                }
            }
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Invalid or expired token"));
                    
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Token validation failed"));
        }
    }
}
