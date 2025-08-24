package com.deliverysystem.apigateway.controller;

import com.deliverysystem.apigateway.dto.UpdateUserRequest;
import com.deliverysystem.apigateway.service.AuthServiceClient;
import com.deliverysystem.apigateway.util.JwtUtil;
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
    public ResponseEntity<String> getUserById(
            @Parameter(description = "User ID") @PathVariable Long id, 
            HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            
            if (!validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"error\": \"Invalid or expired token\"}");
            }
            
            return authServiceClient.getUserById(id, token);
                    
        } catch (Exception e) {
            log.error("Error getting user by ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"User service error: " + e.getMessage() + "\"}");
        }
    }
    
    @GetMapping("/username/{username}")
    public ResponseEntity<String> getUserByUsername(
            @PathVariable String username, 
            HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            
            if (!validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"error\": \"Invalid or expired token\"}");
            }
            
            return authServiceClient.getUserByUsername(username, token);
                    
        } catch (Exception e) {
            log.error("Error getting user by username: {}", username, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"User service error: " + e.getMessage() + "\"}");
        }
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<String> getUserByEmail(
            @PathVariable String email, 
            HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            
            if (!validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"error\": \"Invalid or expired token\"}");
            }
            
            return authServiceClient.getUserByEmail(email, token);
                    
        } catch (Exception e) {
            log.error("Error getting user by email: {}", email, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"User service error: " + e.getMessage() + "\"}");
        }
    }
    
    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieve all users with optional pagination")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<String> getAllUsers(
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
                        .body("{\"error\": \"Invalid or expired token\"}");
            }
            
            String queryParams = String.format("page=%d&size=%d&sortBy=%s&sortDir=%s&paginated=%s", 
                    page, size, sortBy, sortDir, paginated);
            
            // Return exact response from auth service
            return authServiceClient.getAllUsers(token, queryParams);
                    
        } catch (Exception e) {
            log.error("Error getting all users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"User service error: " + e.getMessage() + "\"}");
        }
    }
    
    @GetMapping("/role/{role}")
    public ResponseEntity<String> getUsersByRole(
            @PathVariable String role, 
            HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            
            if (!validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"error\": \"Invalid or expired token\"}");
            }
            
            return authServiceClient.getUsersByRole(role, token);
                    
        } catch (Exception e) {
            log.error("Error getting users by role: {}", role, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"User service error: " + e.getMessage() + "\"}");
        }
    }
    
    @GetMapping("/active")
    public ResponseEntity<String> getActiveUsers(HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            
            if (!validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"error\": \"Invalid or expired token\"}");
            }
            
            return authServiceClient.getActiveUsers(token);
                    
        } catch (Exception e) {
            log.error("Error getting active users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"User service error: " + e.getMessage() + "\"}");
        }
    }
    
    @GetMapping("/inactive")
    public ResponseEntity<String> getInactiveUsers(HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            
            if (!validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"error\": \"Invalid or expired token\"}");
            }
            
            return authServiceClient.getInactiveUsers(token);
                    
        } catch (Exception e) {
            log.error("Error getting inactive users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"User service error: " + e.getMessage() + "\"}");
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
    public ResponseEntity<String> updateUser(
            @Parameter(description = "User ID") @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest updateRequest,
            HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            
            if (!validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"error\": \"Invalid or expired token\"}");
            }
            
            return authServiceClient.updateUser(id, updateRequest, token);
                    
        } catch (Exception e) {
            log.error("Error updating user: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"User service error: " + e.getMessage() + "\"}");
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
    public ResponseEntity<String> deleteUser(
            @Parameter(description = "User ID") @PathVariable Long id,
            HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            
            if (!validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"error\": \"Invalid or expired token\"}");
            }
            
            // Check if user has required role (RESTAURANT)
            String role = jwtUtil.getRoleFromToken(token);
            if (!"RESTAURANT".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("{\"error\": \"Insufficient permissions\"}");
            }
            
            return authServiceClient.deleteUser(id, token);
                    
        } catch (Exception e) {
            log.error("Error deleting user: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"User service error: " + e.getMessage() + "\"}");
        }
    }
    
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<String> deactivateUser(
            @PathVariable Long id,
            HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            
            if (!validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"error\": \"Invalid or expired token\"}");
            }
            
            return authServiceClient.deactivateUser(id, token);
                    
        } catch (Exception e) {
            log.error("Error deactivating user: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"User service error: " + e.getMessage() + "\"}");
        }
    }
    
    @PatchMapping("/{id}/activate")
    public ResponseEntity<String> activateUser(
            @PathVariable Long id,
            HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            
            if (!validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("{\"error\": \"Invalid or expired token\"}");
            }
            
            return authServiceClient.activateUser(id, token);
                    
        } catch (Exception e) {
            log.error("Error activating user: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"User service error: " + e.getMessage() + "\"}");
        }
    }
}
