package com.deliverysystem.authservice.service;

import com.deliverysystem.authservice.dto.*;
import com.deliverysystem.authservice.entity.User;
import com.deliverysystem.authservice.exception.UserAlreadyExistsException;
import com.deliverysystem.authservice.repository.UserRepository;
import com.deliverysystem.authservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering user with username: {}", request.getUsername());
        
        // Check if user already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists: " + request.getUsername());
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists: " + request.getEmail());
        }
        
        // Validate role-specific fields
        if (request.getRole() == User.Role.RESTAURANT) {
            if (request.getRestaurantName() == null || request.getRestaurantName().trim().isEmpty()) {
                throw new IllegalArgumentException("Restaurant name is required for restaurant role");
            }
            if (request.getRestaurantAddress() == null || request.getRestaurantAddress().trim().isEmpty()) {
                throw new IllegalArgumentException("Restaurant address is required for restaurant role");
            }
        } else if (request.getRole() == User.Role.CUSTOMER) {
            // Ensure customers don't have restaurant fields set
            if (request.getRestaurantName() != null && !request.getRestaurantName().trim().isEmpty()) {
                throw new IllegalArgumentException("Restaurant name should not be provided for customer role");
            }
            if (request.getRestaurantAddress() != null && !request.getRestaurantAddress().trim().isEmpty()) {
                throw new IllegalArgumentException("Restaurant address should not be provided for customer role");
            }
        }
        
        // Create new user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .role(request.getRole())
                .restaurantName(request.getRole() == User.Role.RESTAURANT ? request.getRestaurantName() : null)
                .restaurantAddress(request.getRole() == User.Role.RESTAURANT ? request.getRestaurantAddress() : null)
                .isActive(true)
                .build();
        
        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());
        
        // Generate JWT token
        String token = jwtUtil.generateToken(
                savedUser.getUsername(),
                savedUser.getRole().name(),
                savedUser.getId()
        );
        
        UserResponse userResponse = convertToUserResponse(savedUser);
        
        return AuthResponse.builder()
                .accessToken(token)
                .user(userResponse)
                .build();
    }
    
    public AuthResponse login(LoginRequest request) {
        log.info("Attempting login for user: {}", request.getUsernameOrEmail());
        
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsernameOrEmail(),
                            request.getPassword()
                    )
            );
            
            User user = userRepository.findByUsernameOrEmail(request.getUsernameOrEmail())
                    .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
            
            if (!user.getIsActive()) {
                throw new BadCredentialsException("Account is deactivated");
            }
            
            String token = jwtUtil.generateToken(
                    user.getUsername(),
                    user.getRole().name(),
                    user.getId()
            );
            
            UserResponse userResponse = convertToUserResponse(user);
            
            log.info("User logged in successfully: {}", user.getUsername());
            
            return AuthResponse.builder()
                    .accessToken(token)
                    .user(userResponse)
                    .build();
            
        } catch (AuthenticationException e) {
            log.error("Authentication failed for user: {}", request.getUsernameOrEmail());
            throw new BadCredentialsException("Invalid credentials");
        }
    }
    
    private UserResponse convertToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .restaurantName(user.getRestaurantName())
                .restaurantAddress(user.getRestaurantAddress())
                .build();
    }
}
