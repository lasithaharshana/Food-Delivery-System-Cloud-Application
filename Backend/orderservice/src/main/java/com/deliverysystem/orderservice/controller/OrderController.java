package com.deliverysystem.orderservice.controller;

import com.deliverysystem.orderservice.client.FoodClient;
import com.deliverysystem.orderservice.model.Order;
import com.deliverysystem.orderservice.model.OrderItem;
import com.deliverysystem.orderservice.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/order")
@Tag(name = "Order Management", description = "Order management APIs for food delivery")
public class OrderController {

    private final OrderRepository repo;
    private final FoodClient foodClient;
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    public OrderController(OrderRepository repo, FoodClient foodClient) {
        this.repo = repo;
        this.foodClient = foodClient;
    }

    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Order Service is running with MySQL!");
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllItems() {
        return ResponseEntity.ok(repo.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getItem(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping()
    @Operation(summary = "Create new Order", description = "Add a new Order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<?> createItem(@RequestBody Order order) {
        try {
            logger.info("Creating order: {}", order);

            // Validate order
            if (order == null || order.getOrderItems().isEmpty()) {
                return ResponseEntity.badRequest().body("Invalid order data");
            }

            // Extract all foodIds and quantities
            Map<String, Integer> foodQuantities = order.getOrderItems().stream()
                    .collect(Collectors.toMap(
                            item -> item.getFoodId().toString(),
                            OrderItem::getQuantity,
                            Integer::sum));
            logger.info("Food quantities extracted: {}", foodQuantities);

            // Batch check food existence
            Map<Long, Boolean> foodExistMap = foodClient.checkFoodsExist(
                    order.getOrderItems().stream().map(OrderItem::getFoodId).toList());
            logger.info("Food existence map: {}", foodExistMap);

            // Validate all items
            List<Long> invalidIds = order.getOrderItems().stream()
                    .map(OrderItem::getFoodId)
                    .filter(id -> !Boolean.TRUE.equals(foodExistMap.get(id)))
                    .toList();
            if (!invalidIds.isEmpty()) {
                return ResponseEntity.badRequest().body("Invalid food IDs: " + invalidIds);
            }

            // Attach order to items
            order.getOrderItems().forEach(item -> item.setOrder(order));
            try {
                ResponseEntity<String> reduceResponse = foodClient.reduceStock(foodQuantities);
                if (!reduceResponse.getStatusCode().is2xxSuccessful()) {
                    return ResponseEntity.status(reduceResponse.getStatusCode())
                            .body("Failed to reduce stock: " + reduceResponse.getBody());
                }
            } catch (Exception e) {
                logger.error("Error reducing stock: {}", e.getMessage(), e);
                repo.delete(order); // Rollback order creation
                throw e;
            }

            // Save order
            Order saved = repo.save(order);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);

        } catch (Exception e) {
            logger.error("Error creating order: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating order: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateItem(@PathVariable Long id, @RequestBody Order updated) {
        return repo.findById(id).map(existingOrder -> {
            try {
                logger.info("Updating order ID: {}", id);

                if (updated == null || updated.getOrderItems().isEmpty()) {
                    return ResponseEntity.badRequest().body("Invalid order data");
                }

                // Extract new quantities
                Map<String, Integer> newQuantities = updated.getOrderItems().stream()
                        .collect(Collectors.toMap(
                                item -> item.getFoodId().toString(),
                                OrderItem::getQuantity,
                                Integer::sum));

                // Validate food existence
                Map<Long, Boolean> foodExistMap = foodClient.checkFoodsExist(
                        updated.getOrderItems().stream().map(OrderItem::getFoodId).toList());
                List<Long> invalidIds = updated.getOrderItems().stream()
                        .map(OrderItem::getFoodId)
                        .filter(fid -> !Boolean.TRUE.equals(foodExistMap.get(fid)))
                        .toList();
                if (!invalidIds.isEmpty()) {
                    return ResponseEntity.badRequest().body("Invalid food IDs: " + invalidIds);
                }

                // Save snapshot for rollback
                Order oldSnapshot = new Order();
                oldSnapshot.setId(existingOrder.getId());
                oldSnapshot.setCustomerId(existingOrder.getCustomerId());
                oldSnapshot.setNote(existingOrder.getNote());
                oldSnapshot.setStatus(existingOrder.getStatus());
                oldSnapshot.setCost(existingOrder.getCost());
                oldSnapshot.setOrderItems(new ArrayList<>(existingOrder.getOrderItems()));

                // Update basic fields
                existingOrder.setCustomerId(updated.getCustomerId());
                existingOrder.setNote(updated.getNote());
                existingOrder.setStatus(updated.getStatus());
                existingOrder.setCost(updated.getCost());

                // Safe update of collection
                existingOrder.getOrderItems().clear();
                for (OrderItem item : updated.getOrderItems()) {
                    item.setOrder(existingOrder);
                    existingOrder.getOrderItems().add(item);
                }

                // Save updated order
                Order saved = repo.save(existingOrder);

                try {
                    ResponseEntity<String> reduceResponse = foodClient.reduceStock(newQuantities);
                    if (!reduceResponse.getStatusCode().is2xxSuccessful()) {
                        return ResponseEntity.status(reduceResponse.getStatusCode())
                                .body("Failed to reduce stock: " + reduceResponse.getBody());
                    }
                } catch (Exception e) {
                    logger.error("Stock reduction failed, rolling back order {}: {}", id, e.getMessage());
                    repo.save(oldSnapshot); // rollback
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Failed to reduce stock: " + e.getMessage());
                }

                return ResponseEntity.ok(saved);

            } catch (Exception e) {
                logger.error("Error updating order {}: {}", id, e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error updating order: " + e.getMessage());
            }
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable Long id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found");
        }
        repo.deleteById(id);
        return ResponseEntity.ok("Order deleted successfully");
    }
}
