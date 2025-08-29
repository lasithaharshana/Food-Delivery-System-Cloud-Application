package com.deliverysystem.authservice.service;

import com.deliverysystem.authservice.dto.UpdateUserRequest;
import com.deliverysystem.authservice.dto.UserResponse;
import com.deliverysystem.authservice.entity.User;
import com.deliverysystem.authservice.exception.UserAlreadyExistsException;
import com.deliverysystem.authservice.exception.UserNotFoundException;
import com.deliverysystem.authservice.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    
    public UserResponse getUserById(Long id) {
        log.info("Fetching user with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
        return convertToUserResponse(user);
    }
    
    public UserResponse getUserByUsername(String username) {
        log.info("Fetching user with username: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
        return convertToUserResponse(user);
    }
    
    public UserResponse getUserByEmail(String email) {
        log.info("Fetching user with email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        return convertToUserResponse(user);
    }
    
    public List<UserResponse> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll().stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
    }
    
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        log.info("Fetching all users with pagination");
        return userRepository.findAll(pageable)
                .map(this::convertToUserResponse);
    }
    
    public List<UserResponse> getUsersByRole(User.Role role) {
        log.info("Fetching users with role: {}", role);
        return userRepository.findByRole(role).stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
    }
    
    public List<UserResponse> getActiveUsers() {
        log.info("Fetching active users");
        return userRepository.findByIsActive(true).stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
    }
    
    public List<UserResponse> getInactiveUsers() {
        log.info("Fetching inactive users");
        return userRepository.findByIsActive(false).stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        log.info("Updating user with ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
        
        // Check for username conflicts
        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new UserAlreadyExistsException("Username already exists: " + request.getUsername());
            }
            user.setUsername(request.getUsername());
        }
        
        // Check for email conflicts
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new UserAlreadyExistsException("Email already exists: " + request.getEmail());
            }
            user.setEmail(request.getEmail());
        }
        
        // Update other fields
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        
        // Update address for all users
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }
        
        // Update restaurant-specific fields for restaurant users
        if (user.getRole() == User.Role.RESTAURANT) {
            if (request.getRestaurantName() != null) {
                user.setRestaurantName(request.getRestaurantName());
            }
        }
        
        User updatedUser = userRepository.save(user);
        log.info("User updated successfully with ID: {}", updatedUser.getId());
        
        return convertToUserResponse(updatedUser);
    }
    
    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
        
        userRepository.delete(user);
        log.info("User deleted successfully with ID: {}", id);
    }
    
    @Transactional
    public UserResponse deactivateUser(Long id) {
        log.info("Deactivating user with ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
        
        user.setIsActive(false);
        User updatedUser = userRepository.save(user);
        log.info("User deactivated successfully with ID: {}", id);
        
        return convertToUserResponse(updatedUser);
    }
    
    @Transactional
    public UserResponse activateUser(Long id) {
        log.info("Activating user with ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
        
        user.setIsActive(true);
        User updatedUser = userRepository.save(user);
        log.info("User activated successfully with ID: {}", id);
        
        return convertToUserResponse(updatedUser);
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
                .address(user.getAddress())
                .build();
    }

    public boolean isValidRestaurant(Long restaurantId) {
        return userRepository.existsByIdAndRole(restaurantId, User.Role.RESTAURANT);
    }

    public User getRestaurantById(Long restaurantId) {
        return userRepository.findByIdAndRole(restaurantId, User.Role.RESTAURANT)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Restaurant with ID " + restaurantId + " not found"));
    }
}
