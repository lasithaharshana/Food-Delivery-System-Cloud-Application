package com.deliverysystem.apigateway.controller;

import com.deliverysystem.apigateway.dto.ApiResponse;
import com.deliverysystem.apigateway.dto.UpdateUserRequest;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@Slf4j
@Tag(name = "User Management", description = "User CRUD operations through API Gateway")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {
    
    @Autowired
    private AuthServiceClient authServiceClient;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private String extractTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
    
    private boolean validateToken(String token) {
        return token != null && jwtUtil.validateToken(token) && !jwtUtil.isTokenExpired(token);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve user information by user ID")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User found",
                content = @Content(schema = @Schema(implementation = Object.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<Object>> getUserById(
            @Parameter(description = "User ID") @PathVariable Long id, 
            HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            
            if (!validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Invalid or expired token"));
            }
            
            ResponseEntity<String> response = authServiceClient.getUserById(id, token);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode userResponse = objectMapper.readTree(response.getBody());
                return ResponseEntity.ok(ApiResponse.success(userResponse));
            }
            
            return ResponseEntity.status(response.getStatusCode())
                    .body(ApiResponse.error("User not found"));
                    
        } catch (Exception e) {
            log.error("Error getting user by ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("User service error: " + e.getMessage()));
        }
    }
    
    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<Object>> getUserByUsername(
            @PathVariable String username, 
            HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            
            if (!validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Invalid or expired token"));
            }
            
            ResponseEntity<String> response = authServiceClient.getUserByUsername(username, token);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode userResponse = objectMapper.readTree(response.getBody());
                return ResponseEntity.ok(ApiResponse.success(userResponse));
            }
            
            return ResponseEntity.status(response.getStatusCode())
                    .body(ApiResponse.error("User not found"));
                    
        } catch (Exception e) {
            log.error("Error getting user by username: {}", username, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("User service error: " + e.getMessage()));
        }
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<Object>> getUserByEmail(
            @PathVariable String email, 
            HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            
            if (!validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Invalid or expired token"));
            }
            
            ResponseEntity<String> response = authServiceClient.getUserByEmail(email, token);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode userResponse = objectMapper.readTree(response.getBody());
                return ResponseEntity.ok(ApiResponse.success(userResponse));
            }
            
            return ResponseEntity.status(response.getStatusCode())
                    .body(ApiResponse.error("User not found"));
                    
        } catch (Exception e) {
            log.error("Error getting user by email: {}", email, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("User service error: " + e.getMessage()));
        }
    }
    
    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieve all users with optional pagination")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<Object>> getAllUsers(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String sortDir,
            @Parameter(description = "Enable pagination") @RequestParam(defaultValue = "false") boolean paginated,
            HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            
            if (!validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Invalid or expired token"));
            }
            
            String queryParams = String.format("page=%d&size=%d&sortBy=%s&sortDir=%s&paginated=%s", 
                    page, size, sortBy, sortDir, paginated);
            
            ResponseEntity<String> response = authServiceClient.getAllUsers(token, queryParams);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode usersResponse = objectMapper.readTree(response.getBody());
                return ResponseEntity.ok(ApiResponse.success(usersResponse));
            }
            
            return ResponseEntity.status(response.getStatusCode())
                    .body(ApiResponse.error("Failed to retrieve users"));
                    
        } catch (Exception e) {
            log.error("Error getting all users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("User service error: " + e.getMessage()));
        }
    }
    
    @GetMapping("/role/{role}")
    public ResponseEntity<ApiResponse<Object>> getUsersByRole(
            @PathVariable String role, 
            HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            
            if (!validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Invalid or expired token"));
            }
            
            ResponseEntity<String> response = authServiceClient.getUsersByRole(role, token);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode usersResponse = objectMapper.readTree(response.getBody());
                return ResponseEntity.ok(ApiResponse.success(usersResponse));
            }
            
            return ResponseEntity.status(response.getStatusCode())
                    .body(ApiResponse.error("Failed to retrieve users by role"));
                    
        } catch (Exception e) {
            log.error("Error getting users by role: {}", role, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("User service error: " + e.getMessage()));
        }
    }
    
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<Object>> getActiveUsers(HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            
            if (!validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Invalid or expired token"));
            }
            
            ResponseEntity<String> response = authServiceClient.getActiveUsers(token);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode usersResponse = objectMapper.readTree(response.getBody());
                return ResponseEntity.ok(ApiResponse.success(usersResponse));
            }
            
            return ResponseEntity.status(response.getStatusCode())
                    .body(ApiResponse.error("Failed to retrieve active users"));
                    
        } catch (Exception e) {
            log.error("Error getting active users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("User service error: " + e.getMessage()));
        }
    }
    
    @GetMapping("/inactive")
    public ResponseEntity<ApiResponse<Object>> getInactiveUsers(HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            
            if (!validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Invalid or expired token"));
            }
            
            ResponseEntity<String> response = authServiceClient.getInactiveUsers(token);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode usersResponse = objectMapper.readTree(response.getBody());
                return ResponseEntity.ok(ApiResponse.success(usersResponse));
            }
            
            return ResponseEntity.status(response.getStatusCode())
                    .body(ApiResponse.error("Failed to retrieve inactive users"));
                    
        } catch (Exception e) {
            log.error("Error getting inactive users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("User service error: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Update user information")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User updated successfully",
                content = @Content(schema = @Schema(implementation = Object.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<Object>> updateUser(
            @Parameter(description = "User ID") @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest updateRequest,
            HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            
            if (!validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Invalid or expired token"));
            }
            
            ResponseEntity<String> response = authServiceClient.updateUser(id, updateRequest, token);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode userResponse = objectMapper.readTree(response.getBody());
                return ResponseEntity.ok(ApiResponse.success(userResponse));
            }
            
            return ResponseEntity.status(response.getStatusCode())
                    .body(ApiResponse.error("Failed to update user"));
                    
        } catch (Exception e) {
            log.error("Error updating user: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("User service error: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Delete user permanently (RESTAURANT role required)")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User deleted successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<String>> deleteUser(
            @Parameter(description = "User ID") @PathVariable Long id,
            HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            
            if (!validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Invalid or expired token"));
            }
            
            // Check if user has required role (RESTAURANT)
            String role = jwtUtil.getRoleFromToken(token);
            if (!"RESTAURANT".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("Insufficient permissions"));
            }
            
            ResponseEntity<String> response = authServiceClient.deleteUser(id, token);
            
            if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                return ResponseEntity.ok(ApiResponse.success("User deleted successfully"));
            }
            
            return ResponseEntity.status(response.getStatusCode())
                    .body(ApiResponse.error("Failed to delete user"));
                    
        } catch (Exception e) {
            log.error("Error deleting user: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("User service error: " + e.getMessage()));
        }
    }
    
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<Object>> deactivateUser(
            @PathVariable Long id,
            HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            
            if (!validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Invalid or expired token"));
            }
            
            ResponseEntity<String> response = authServiceClient.deactivateUser(id, token);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode userResponse = objectMapper.readTree(response.getBody());
                return ResponseEntity.ok(ApiResponse.success(userResponse));
            }
            
            return ResponseEntity.status(response.getStatusCode())
                    .body(ApiResponse.error("Failed to deactivate user"));
                    
        } catch (Exception e) {
            log.error("Error deactivating user: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("User service error: " + e.getMessage()));
        }
    }
    
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<Object>> activateUser(
            @PathVariable Long id,
            HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            
            if (!validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Invalid or expired token"));
            }
            
            ResponseEntity<String> response = authServiceClient.activateUser(id, token);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode userResponse = objectMapper.readTree(response.getBody());
                return ResponseEntity.ok(ApiResponse.success(userResponse));
            }
            
            return ResponseEntity.status(response.getStatusCode())
                    .body(ApiResponse.error("Failed to activate user"));
                    
        } catch (Exception e) {
            log.error("Error activating user: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("User service error: " + e.getMessage()));
        }
    }
}
