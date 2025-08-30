package com.deliverysystem.foodservice.controller;

import com.deliverysystem.foodservice.model.Food;
import com.deliverysystem.foodservice.repository.FoodRepository;
import com.deliverysystem.foodservice.client.AuthClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/foods")
@Tag(name = "Food Management", description = "Food management APIs for Food items")
public class FoodController {

    private final FoodRepository repo;
    private final AuthClient userClient;
    private static final Logger logger = LoggerFactory.getLogger(FoodController.class);

    public FoodController(FoodRepository repo, AuthClient userClient) {
        this.repo = repo;
        this.userClient = userClient;
    }

    @GetMapping("/test")
    @Operation(summary = "Test endpoint", description = "Check if the food service is running")
    @ApiResponse(responseCode = "200", description = "Service is running")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Food Service is running with MySQL!");
    }

    @GetMapping
    @Operation(summary = "Get all Foods", description = "Retrieve a list of all Foods")
    @ApiResponse(responseCode = "200", description = "List of Foods retrieved successfully")
    public ResponseEntity<List<Food>> getAllItems() {
        return ResponseEntity.ok(repo.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Food by ID", description = "Retrieve a specific Food by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Food found"),
            @ApiResponse(responseCode = "404", description = "Food not found")
    })
    public ResponseEntity<Food> getItem(@Parameter(description = "Food item ID") @PathVariable Long id) {
        Optional<Food> food = repo.findById(id);
        return food.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    @Operation(summary = "Create new Food item", description = "Add a new item to the Food")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Food item created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<?> createItem(@RequestBody Food item) {
        try {
            Long restaurantId = item.getRestaurantId();
            if (restaurantId != null) {
                // Call UserClient to validate restaurant
                Map<String, Object> response = userClient.checkRestaurantExists(restaurantId.toString());

                if (response != null && Boolean.TRUE.equals(response.get("valid"))) {
                    Food saved = repo.save(item);
                    return ResponseEntity.status(HttpStatus.CREATED).body(saved);
                }
            }
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid or non-existing restaurant"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Food", description = "Update an existing Food")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Food updated successfully"),
            @ApiResponse(responseCode = "404", description = "Food not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<Food> updateItem(@Parameter(description = "Food ID") @PathVariable Long id,
            @RequestBody Food updated) {
        Long restaurantId = updated.getRestaurantId();
        if (restaurantId != null) {
            Map<String, Object> response = userClient.checkRestaurantExists(restaurantId.toString());
            if (response != null && Boolean.TRUE.equals(response.get("valid"))) {
                return repo.findById(id).map(item -> {
                    item.setName(updated.getName());
                    item.setDescription(updated.getDescription());
                    item.setPrice(updated.getPrice());
                    item.setQuantity(updated.getQuantity());
                    item.setCategory(updated.getCategory());
                    item.setImageUrl(updated.getImageUrl());
                    item.setStatus(updated.getStatus());
                    item.setPopular(updated.getPopular());
                    Food saved = repo.save(item);
                    return ResponseEntity.ok(saved);
                }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
            } else {
                return ResponseEntity.badRequest().build();
            }
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Food", description = "Remove a Food from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Food deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Food not found")
    })
    public ResponseEntity<Void> deleteItem(@Parameter(description = "Food ID") @PathVariable Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/{foodId}/exists")
    @Operation(summary = "Check if food exists", description = "Verify if a food item exists by ID")
    public ResponseEntity<Boolean> checkFoodExists(@PathVariable Long id) {
        boolean exists = repo.existsById(id);
        return ResponseEntity.ok(exists);
    }

    

}