package com.deliverysystem.inventoryservice.controller;

import com.deliverysystem.inventoryservice.model.InventoryItem;
import com.deliverysystem.inventoryservice.repository.InventoryRepository;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@Tag(name = "Inventory Management", description = "Inventory management APIs for food items")
public class InventoryController {

    private final InventoryRepository repo;

    public InventoryController(InventoryRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/test")
    @Operation(summary = "Test endpoint", description = "Check if the inventory service is running")
    @ApiResponse(responseCode = "200", description = "Service is running")
    public String testEndpoint() {
        return "Inventory Service is running with MySQL!";
    }

    @GetMapping
    @Operation(summary = "Get all inventory items", description = "Retrieve a list of all inventory items")
    @ApiResponse(responseCode = "200", description = "List of inventory items retrieved successfully")
    public List<InventoryItem> getAllItems() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get inventory item by ID", description = "Retrieve a specific inventory item by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory item found"),
            @ApiResponse(responseCode = "404", description = "Inventory item not found")
    })
    public InventoryItem getItem(@Parameter(description = "Inventory item ID") @PathVariable Integer id) {
        return repo.findById(id).orElse(null);
    }

    @PostMapping
    @Operation(summary = "Create new inventory item", description = "Add a new item to the inventory")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Inventory item created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public InventoryItem createItem(@RequestBody InventoryItem item) {
        return repo.save(item);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update inventory item", description = "Update an existing inventory item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory item updated successfully"),
            @ApiResponse(responseCode = "404", description = "Inventory item not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public InventoryItem updateItem(@Parameter(description = "Inventory item ID") @PathVariable Integer id, @RequestBody InventoryItem updated) {
        return repo.findById(id).map(item -> {
            item.setName(updated.getName());
            item.setQty(updated.getQty());
            item.setDescription(updated.getDescription());
            return repo.save(item);
        }).orElse(null);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete inventory item", description = "Remove an inventory item from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory item deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Inventory item not found")
    })
    public String deleteItem(@Parameter(description = "Inventory item ID") @PathVariable Integer id) {
        repo.deleteById(id);
        return "Item deleted";
    }
}
