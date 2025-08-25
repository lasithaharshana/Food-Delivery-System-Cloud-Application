package com.deliverysystem.orderservice.controller;

import com.deliverysystem.orderservice.client.RestaurantClient;
import com.deliverysystem.orderservice.model.Order;
import com.deliverysystem.orderservice.repository.OrderRepository;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import org.slf4j.Logger;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@Tag(name = "Order Management", description = "Order management APIs for food delivery")
public class OrderController {

    private final OrderRepository repo;
    private final RestaurantClient restaurantClient;
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    public OrderController(OrderRepository repo, RestaurantClient restaurantClient) {
        this.repo = repo;
        this.restaurantClient = restaurantClient;
    }

    @GetMapping("/test")
    @Operation(summary = "Test endpoint", description = "Check if the order service is running")
    @ApiResponse(responseCode = "200", description = "Service is running")
    public String testEndpoint() {
        return "Order Service is running with MySQL!";
    }

    @GetMapping
    @Operation(summary = "Get all orders", description = "Retrieve a list of all orders")
    @ApiResponse(responseCode = "200", description = "List of orders retrieved successfully")
    public List<Order> getAllItems() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID", description = "Retrieve a specific order by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public Order getItem(@Parameter(description = "Order ID") @PathVariable Integer id) {
        return repo.findById(id).orElse(null);
    }

    @PostMapping
    @Operation(summary = "Create new order", description = "Place a new order in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data or restaurant does not exist")
    })
    public ResponseEntity<?> createItem(@RequestBody Order item) {
        try {
            // ✅ Validate restaurant ID using RestaurantClient
            Boolean exists = restaurantClient.checkRestaurantExists(item.getRestaurantId());
            if (exists == null || !exists) {
                return ResponseEntity.badRequest().body("Invalid restaurant ID: " + item.getRestaurantId());
            }
            logger.info("Creating order for restaurant ID: {}", item.getRestaurantId());
            logger.debug("Order details: {}", item);
            return ResponseEntity.status(201).body(repo.save(item));
        } catch (Exception e) {
            logger.error("Error creating order: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error creating order: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update order", description = "Update an existing order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order updated successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data or restaurant does not exist")
    })
    public ResponseEntity<?> updateItem(@Parameter(description = "Order ID") @PathVariable Integer id,
                                        @RequestBody Order updated) {
        return repo.findById(id)
                .map(item -> {
                    // ✅ Validate restaurant before updating
                    Boolean exists = restaurantClient.checkRestaurantExists(updated.getRestaurantId());
                    if (exists == null || !exists) {
                        return ResponseEntity.badRequest().body("Invalid restaurant ID: " + updated.getRestaurantId());
                    }
                    item.setRestaurantId(updated.getRestaurantId());
                    item.setCustomerId(updated.getCustomerId());
                    item.setCustomerName(updated.getCustomerName());
                    item.setCustomerPhoneNumber(updated.getCustomerPhoneNumber());
                    item.setNote(updated.getNote());
                    item.setStatus(updated.getStatus());
                    item.setCost(updated.getCost());

                    // Clear existing items (orphanRemoval removes them from DB)
                    item.removeOrderItems();

                    // Add updated items
                    item.setOrderItems(updated.getOrderItems());

                    return ResponseEntity.ok(repo.save(item));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete order", description = "Cancel/delete an order from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<String> deleteItem(@Parameter(description = "Order ID") @PathVariable Integer id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repo.deleteById(id);
        return ResponseEntity.ok("Item deleted");
    }
}
