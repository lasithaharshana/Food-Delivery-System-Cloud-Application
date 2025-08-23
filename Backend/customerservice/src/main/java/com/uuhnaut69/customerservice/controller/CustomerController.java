package com.uuhnaut69.customerservice.controller;

import com.uuhnaut69.customerservice.dto.AuthRequest;
import com.uuhnaut69.customerservice.dto.CustomerRequest;
import com.uuhnaut69.customerservice.dto.CustomerResponse;
import com.uuhnaut69.customerservice.model.Customer;
import com.uuhnaut69.customerservice.service.CustomerService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
@Tag(name = "Customer Management", description = "Customer management APIs")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new customer", description = "Register a new customer in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Customer created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Customer already exists")
    })
    public CustomerResponse create(@RequestBody @Valid CustomerRequest req) {
        Customer created = customerService.create(req);
        return toResponse(created);
    }

    @GetMapping
    @Operation(summary = "Get all customers", description = "Retrieve a list of all customers")
    @ApiResponse(responseCode = "200", description = "List of customers retrieved successfully")
    public List<CustomerResponse> list() {
        return customerService.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID", description = "Retrieve a specific customer by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer found"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public CustomerResponse get(@Parameter(description = "Customer ID") @PathVariable UUID id) {
        return toResponse(customerService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update customer", description = "Update an existing customer's information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer updated successfully"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public CustomerResponse update(@Parameter(description = "Customer ID") @PathVariable UUID id, @RequestBody @Valid CustomerRequest req) {
        return toResponse(customerService.update(id, req));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete customer", description = "Delete a customer from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Customer deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public void delete(@Parameter(description = "Customer ID") @PathVariable UUID id) {
        customerService.delete(id);
    }

    // Authentication endpoint kept inside the same service
    @PostMapping("/auth/login")
    @Operation(summary = "Customer Login (Alternative endpoint)", description = "Alternative authentication endpoint")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public Map<String, String> login(@RequestBody AuthRequest req) {
        String token = customerService.authenticate(req);
        return Map.of("token", token);
    }

    private CustomerResponse toResponse(Customer c) {
        return CustomerResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .email(c.getEmail())
                .createdAt(c.getCreatedAt())
                .build();
    }
}
