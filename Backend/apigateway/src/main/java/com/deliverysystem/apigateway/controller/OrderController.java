package com.deliverysystem.apigateway.controller;

import com.deliverysystem.apigateway.service.OrderServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('CUSTOMER')")
public class OrderController {
    
    @Autowired
    private OrderServiceClient orderServiceClient;
    
    @GetMapping("/my-orders")
    public ResponseEntity<String> getMyOrders(HttpServletRequest request,
                                            @RequestHeader("Authorization") String authHeader) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            String token = authHeader.substring(7); // Remove "Bearer " prefix
            
            return orderServiceClient.getOrders(token, userId);
            
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("{\"success\": false, \"message\": \"Error retrieving orders: " + e.getMessage() + "\"}");
        }
    }
    
    @PostMapping
    public ResponseEntity<String> createOrder(HttpServletRequest request,
                                            @RequestHeader("Authorization") String authHeader,
                                            @RequestBody Object orderRequest) {
        try {
            String token = authHeader.substring(7); // Remove "Bearer " prefix
            
            return orderServiceClient.createOrder(token, orderRequest);
            
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("{\"success\": false, \"message\": \"Error creating order: " + e.getMessage() + "\"}");
        }
    }
    
    @GetMapping("/**")
    public ResponseEntity<String> proxyGetRequest(HttpServletRequest request,
                                                @RequestHeader HttpHeaders headers) {
        try {
            String path = request.getRequestURI().replaceFirst("/api/orders", "/api/orders");
            return orderServiceClient.proxyRequest(path, HttpMethod.GET, headers, null);
            
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("{\"success\": false, \"message\": \"Error processing request: " + e.getMessage() + "\"}");
        }
    }
    
    @PostMapping("/**")
    public ResponseEntity<String> proxyPostRequest(HttpServletRequest request,
                                                 @RequestHeader HttpHeaders headers,
                                                 @RequestBody(required = false) Object body) {
        try {
            String path = request.getRequestURI().replaceFirst("/api/orders", "/api/orders");
            return orderServiceClient.proxyRequest(path, HttpMethod.POST, headers, body);
            
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("{\"success\": false, \"message\": \"Error processing request: " + e.getMessage() + "\"}");
        }
    }
    
    @PutMapping("/**")
    public ResponseEntity<String> proxyPutRequest(HttpServletRequest request,
                                                @RequestHeader HttpHeaders headers,
                                                @RequestBody(required = false) Object body) {
        try {
            String path = request.getRequestURI().replaceFirst("/api/orders", "/api/orders");
            return orderServiceClient.proxyRequest(path, HttpMethod.PUT, headers, body);
            
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("{\"success\": false, \"message\": \"Error processing request: " + e.getMessage() + "\"}");
        }
    }
    
    @DeleteMapping("/**")
    public ResponseEntity<String> proxyDeleteRequest(HttpServletRequest request,
                                                   @RequestHeader HttpHeaders headers) {
        try {
            String path = request.getRequestURI().replaceFirst("/api/orders", "/api/orders");
            return orderServiceClient.proxyRequest(path, HttpMethod.DELETE, headers, null);
            
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("{\"success\": false, \"message\": \"Error processing request: " + e.getMessage() + "\"}");
        }
    }
}
