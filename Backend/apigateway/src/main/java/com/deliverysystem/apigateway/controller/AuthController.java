package com.deliverysystem.apigateway.controller;

import com.deliverysystem.apigateway.dto.ApiResponse;
import com.deliverysystem.apigateway.dto.AuthRequest;
import com.deliverysystem.apigateway.dto.AuthResponse;
import com.deliverysystem.apigateway.dto.RegisterRequest;
import com.deliverysystem.apigateway.service.AuthServiceClient;
import com.deliverysystem.apigateway.util.JwtUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private AuthServiceClient authServiceClient;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @PostMapping("/login")
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
    public ResponseEntity<ApiResponse<String>> validateToken(@RequestHeader("Authorization") String authHeader) {
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
