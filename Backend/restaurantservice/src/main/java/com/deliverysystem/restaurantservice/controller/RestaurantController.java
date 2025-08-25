package com.deliverysystem.restaurantservice.controller;

import com.deliverysystem.restaurantservice.model.Restaurant;
import com.deliverysystem.restaurantservice.repository.RestaurantRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
@Tag(name = "Restaurant Management", description = "Restaurant management APIs for food items")
public class RestaurantController {

    private final RestaurantRepository repo;

    public RestaurantController(RestaurantRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/test")
    @Operation(summary = "Test endpoint", description = "Check if the restaurant service is running")
    @ApiResponse(responseCode = "200", description = "Service is running")
    public String testEndpoint() {
        return "Restaurant Service is running with MySQL!";
    }

    @GetMapping
    @Operation(summary = "Get all restaurants", description = "Retrieve a list of all restaurants")
    @ApiResponse(responseCode = "200", description = "List of restaurants retrieved successfully")
    public List<Restaurant> getAllItems() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get restaurant by ID", description = "Retrieve a specific restaurant by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurant found"),
            @ApiResponse(responseCode = "404", description = "Restaurant not found")
    })
    public Restaurant getItem(@Parameter(description = "Restaurant item ID") @PathVariable Integer id) {
        return repo.findById(id).orElse(null);
    }

    @PostMapping
    @Operation(summary = "Create new restaurant item", description = "Add a new item to the restaurant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Restaurant item created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public Restaurant createItem(@RequestBody Restaurant item) {
        return repo.save(item);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update restaurant", description = "Update an existing restaurant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurant updated successfully"),
            @ApiResponse(responseCode = "404", description = "Restaurant not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public Restaurant updateItem(@Parameter(description = "Restaurant ID") @PathVariable Integer id,
            @RequestBody Restaurant updated) {
        return repo.findById(id).map(item -> {
            item.setName(updated.getName());
            item.setDescription(updated.getDescription());
            item.setPrice(updated.getPrice());
            item.setCategory(updated.getCategory());
            item.setImageUrl(updated.getImageUrl());
            item.setStatus(updated.getStatus());
            item.setPopular(updated.getPopular());
            return repo.save(item);
        }).orElse(null);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete restaurant", description = "Remove a restaurant from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurant deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Restaurant not found")
    })
    public String deleteItem(@Parameter(description = "Restaurant ID") @PathVariable Integer id) {
        repo.deleteById(id);
        return "Item deleted";
    }

    @GetMapping("/{restaurantId}/exists")
    public ResponseEntity<Boolean> checkRestaurantExists(@PathVariable String restaurantId) {
        boolean exists = repo.existsByRestaurantId(restaurantId);
        return ResponseEntity.ok(exists);
    }

}
