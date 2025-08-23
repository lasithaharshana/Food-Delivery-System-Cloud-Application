package com.deliverysystem.orderservice.controller;
import com.deliverysystem.orderservice.model.Order;
import com.deliverysystem.orderservice.repository.*;

import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;


import java.util.List;

@RestController
@RequestMapping("/api/order")
@Tag(name = "Order Management", description = "Order management APIs for food delivery")
public class OrderController {

    private final OrderRepository repo;

    public OrderController(OrderRepository repo) {
        this.repo = repo;
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
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public Order createItem(@RequestBody Order item) {
        try {
            return repo.save(item);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update order", description = "Update an existing order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order updated successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public Order updateItem(@Parameter(description = "Order ID") @PathVariable Integer id, @RequestBody Order updated) {
        Order item = repo.findById(id).orElse(null);
        if (item != null) {
            item.setName(updated.getName());
            item.setQty(updated.getQty());
            item.setDescription(updated.getDescription());
            item.setCustomerName(updated.getCustomerName());
            item.setAddress(updated.getAddress());
            item.setOrderDate(updated.getOrderDate());
            item.setTotalCost(updated.getTotalCost());
            return repo.save(item);
        }
        return null;
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete order", description = "Cancel/delete an order from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public String deleteItem(@Parameter(description = "Order ID") @PathVariable Integer id) {
        repo.deleteById(id);
        return "Item deleted";
    }
}
