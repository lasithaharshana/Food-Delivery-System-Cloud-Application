package com.deliverysystem.orderservice.controller;
import com.deliverysystem.orderservice.model.Order;
import com.deliverysystem.orderservice.repository.*;

import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final OrderRepository repo;

    public OrderController(OrderRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/test")
    public String testEndpoint() {
        return "Order Service is running with MySQL!";
    }

    @GetMapping
    public List<Order> getAllItems() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public Order getItem(@PathVariable Integer id) {
        return repo.findById(id).orElse(null);
    }

    @PostMapping
    public Order createItem(@RequestBody Order item) {
        try {
            return repo.save(item);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PutMapping("/{id}")
    public Order updateItem(@PathVariable Integer id, @RequestBody Order updated) {
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
    public String deleteItem(@PathVariable Integer id) {
        repo.deleteById(id);
        return "Item deleted";
    }
}
