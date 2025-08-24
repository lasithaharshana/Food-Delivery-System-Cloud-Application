package com.deliverysystem.authservice.service;

import com.deliverysystem.authservice.dto.RegisterRequest;
import com.deliverysystem.authservice.entity.User;
import com.deliverysystem.authservice.repository.UserRepository;
import com.deliverysystem.authservice.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest customerRequest;
    private RegisterRequest restaurantRequest;

    @BeforeEach
    void setUp() {
        // Customer registration request
        customerRequest = RegisterRequest.builder()
                .username("customer_user")
                .email("customer@example.com")
                .password("password123")
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("+1234567890")
                .role(User.Role.CUSTOMER)
                .build();

        // Restaurant registration request
        restaurantRequest = RegisterRequest.builder()
                .username("restaurant_user")
                .email("restaurant@example.com")
                .password("password123")
                .firstName("Mario")
                .lastName("Rossi")
                .phoneNumber("+1234567891")
                .role(User.Role.RESTAURANT)
                .restaurantName("Mario's Pizza")
                .restaurantAddress("123 Pizza Street")
                .build();
    }

    @Test
    void register_CustomerRole_Success() {
        // Arrange
        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });
        when(jwtUtil.generateToken(any(), any(), any())).thenReturn("mock-jwt-token");

        // Act & Assert
        assertDoesNotThrow(() -> authService.register(customerRequest));
    }

    @Test
    void register_RestaurantRole_Success() {
        // Arrange
        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });
        when(jwtUtil.generateToken(any(), any(), any())).thenReturn("mock-jwt-token");

        // Act & Assert
        assertDoesNotThrow(() -> authService.register(restaurantRequest));
    }

    @Test
    void register_CustomerWithRestaurantFields_ThrowsException() {
        // Arrange
        RegisterRequest invalidCustomer = RegisterRequest.builder()
                .username("invalid_customer")
                .email("invalid@customer.com")
                .password("password123")
                .firstName("John")
                .lastName("Doe")
                .role(User.Role.CUSTOMER)
                .restaurantName("Should Not Be Here")
                .build();

        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.register(invalidCustomer)
        );
        assertEquals("Restaurant name should not be provided for customer role", exception.getMessage());
    }

    @Test
    void register_RestaurantWithoutName_ThrowsException() {
        // Arrange
        RegisterRequest invalidRestaurant = RegisterRequest.builder()
                .username("invalid_restaurant")
                .email("invalid@restaurant.com")
                .password("password123")
                .firstName("Mario")
                .lastName("Rossi")
                .role(User.Role.RESTAURANT)
                .restaurantAddress("123 Pizza Street")
                // Missing restaurantName
                .build();

        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.register(invalidRestaurant)
        );
        assertEquals("Restaurant name is required for restaurant role", exception.getMessage());
    }

    @Test
    void register_RestaurantWithoutAddress_ThrowsException() {
        // Arrange
        RegisterRequest invalidRestaurant = RegisterRequest.builder()
                .username("invalid_restaurant")
                .email("invalid@restaurant.com")
                .password("password123")
                .firstName("Mario")
                .lastName("Rossi")
                .role(User.Role.RESTAURANT)
                .restaurantName("Mario's Pizza")
                // Missing restaurantAddress
                .build();

        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.register(invalidRestaurant)
        );
        assertEquals("Restaurant address is required for restaurant role", exception.getMessage());
    }
}
