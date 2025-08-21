package com.deliverysystem.inventoryservice.controller;

import com.deliverysystem.inventoryservice.model.InventoryItem;
import com.deliverysystem.inventoryservice.repository.InventoryRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryRepository repo;

    public InventoryController(InventoryRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/test")
    public String testEndpoint() {
        return "Inventory Service is running with MySQL!";
    }

    @GetMapping
    public List<InventoryItem> getAllItems() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public InventoryItem getItem(@PathVariable Integer id) {
        return repo.findById(id).orElse(null);
    }

    @PostMapping
    public InventoryItem createItem(@RequestBody InventoryItem item) {
        return repo.save(item);
    }

    @PutMapping("/{id}")
    public InventoryItem updateItem(@PathVariable Integer id, @RequestBody InventoryItem updated) {
        return repo.findById(id).map(item -> {
            item.setName(updated.getName());
            item.setQty(updated.getQty());
            item.setDescription(updated.getDescription());
            return repo.save(item);
        }).orElse(null);
    }

    @DeleteMapping("/{id}")
    public String deleteItem(@PathVariable Integer id) {
        repo.deleteById(id);
        return "Item deleted";
    }
}
