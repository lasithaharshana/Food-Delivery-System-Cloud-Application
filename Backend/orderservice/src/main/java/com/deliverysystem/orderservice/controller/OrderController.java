package com.deliverysystem.orderservice.controller;

import com.deliverysystem.orderservice.client.FoodClient;
import com.deliverysystem.orderservice.dto.UserResponse;
import com.deliverysystem.orderservice.dto.ValidateTokenRequest;
import com.deliverysystem.orderservice.client.AuthClient;
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
    private final AuthClient authClient;

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    public OrderController(OrderRepository repo, FoodClient foodClient, AuthClient authClient) {
        this.repo = repo;
        this.foodClient = foodClient;
        this.authClient = authClient;
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

    @PostMapping
    @Operation(summary = "Create new Order", description = "Add a new Order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<?> createItem(@RequestBody Order order,
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
            }
            String token = authorizationHeader.substring(7);
            logger.info("Validating token: {}", token);
            ValidateTokenRequest request = new ValidateTokenRequest(token);
            UserResponse customer = authClient.validateToken(request);

            if (customer == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid customer");
            }
            order.setCustomerId(customer.getId());

            if (order == null || order.getOrderItems().isEmpty()) {
                return ResponseEntity.badRequest().body("Invalid order data");
            }

            if (order == null || order.getOrderItems().isEmpty()) {
                return ResponseEntity.badRequest().body("Invalid order data");
            }

            Map<String, Integer> foodQuantities = order.getOrderItems().stream()
                    .collect(Collectors.toMap(
                            item -> item.getFoodId().toString(),
                            OrderItem::getQuantity,
                            Integer::sum));
            logger.info("Food quantities extracted: {}", foodQuantities);

            Map<Long, Boolean> foodExistMap = foodClient.checkFoodsExist(
                    order.getOrderItems().stream().map(OrderItem::getFoodId).toList());
            logger.info("Food existence map: {}", foodExistMap);

            List<Long> invalidIds = order.getOrderItems().stream()
                    .map(OrderItem::getFoodId)
                    .filter(id -> !Boolean.TRUE.equals(foodExistMap.get(id)))
                    .toList();

            if (!invalidIds.isEmpty()) {
                return ResponseEntity.badRequest().body("Invalid food IDs: " + invalidIds);
            }

            order.getOrderItems().forEach(item -> item.setOrder(order));

            try {
                ResponseEntity<String> reduceResponse = foodClient.reduceStock(foodQuantities);
                if (!reduceResponse.getStatusCode().is2xxSuccessful()) {
                    return ResponseEntity.status(reduceResponse.getStatusCode())
                            .body("Failed to reduce stock: " + reduceResponse.getBody());
                }
            } catch (Exception e) {
                logger.error("Error reducing stock: {}", e.getMessage(), e);
                repo.delete(order);
                throw e;
            }

            Order saved = repo.save(order);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);

        } catch (Exception e) {
            logger.error("Error creating order: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating order: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateItem(
            @PathVariable Long id,
            @RequestBody Order updated,
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
            }
            String token = authorizationHeader.substring(7);
            ValidateTokenRequest request = new ValidateTokenRequest(token);
            UserResponse user = authClient.validateToken(request);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user");
            }

            return repo.findById(id).map(existingOrder -> {
                try {
                    logger.info("Updating order ID: {}", id);

                    if (updated == null || updated.getOrderItems().isEmpty()) {
                        return ResponseEntity.badRequest().body("Invalid order data");
                    }

                    Map<String, Integer> newQuantities = updated.getOrderItems().stream()
                            .collect(Collectors.toMap(
                                    item -> item.getFoodId().toString(),
                                    OrderItem::getQuantity,
                                    Integer::sum));

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

                    // ✅ Do not override customerId from request — keep token user’s ID
                    existingOrder.setCustomerId(existingOrder.getCustomerId());
                    existingOrder.setNote(updated.getNote());
                    existingOrder.setStatus(updated.getStatus());
                    existingOrder.setCost(updated.getCost());

                    existingOrder.getOrderItems().clear();
                    for (OrderItem item : updated.getOrderItems()) {
                        item.setOrder(existingOrder);
                        existingOrder.getOrderItems().add(item);
                    }

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

        } catch (Exception e) {
            logger.error("Error updating order: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating order: " + e.getMessage());
        }
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
