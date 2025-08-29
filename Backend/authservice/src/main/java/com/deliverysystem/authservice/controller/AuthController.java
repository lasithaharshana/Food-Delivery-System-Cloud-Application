package com.deliverysystem.authservice.controller;
import org.springframework.security.core.Authentication;
import com.deliverysystem.authservice.dto.AuthResponse;
import com.deliverysystem.authservice.dto.LoginRequest;
import com.deliverysystem.authservice.dto.RegisterRequest;
import com.deliverysystem.authservice.dto.UserResponse;
import com.deliverysystem.authservice.dto.ValidateTokenRequest;
import com.deliverysystem.authservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication and registration endpoints")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Register a new user with role (CUSTOMER or RESTAURANT). Restaurant users must provide restaurant details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully", content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "User already exists")
    })
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Registration request received for username: {}", request.getUsername());
        AuthResponse response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user with username/email and password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful", content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request received for user: {}", request.getUsernameOrEmail());
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate")
    @Operation(
        summary = "Validate JWT token",
        description = "Validate the provided JWT token and return user information"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token is valid and user data returned",
                content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid or expired token")
    })
    public ResponseEntity<UserResponse> validateToken(@Valid @RequestBody ValidateTokenRequest request) {
        try {
            String token = request.getToken();
            
            if (token == null || token.trim().isEmpty()) {
                log.warn("Empty token provided for validation");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            UserResponse user = authService.validateTokenAndGetUser(token);
            return ResponseEntity.ok(user);
                    
        } catch (Exception e) {
            log.error("Token validation error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/userinfo")
    @Operation(summary = "Get authenticated user details", description = "Fetch the current user details from SecurityContext")
    public ResponseEntity<?> getUserInfo(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return ResponseEntity.ok(Map.of(
            "username", userDetails.getUsername(),
            "authorities", userDetails.getAuthorities()
        ));
    }

}
