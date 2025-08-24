package com.deliverysystem.apigateway.controller;

import com.deliverysystem.apigateway.service.InventoryServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('RESTAURANT')")
public class InventoryController {
    
    @Autowired
    private InventoryServiceClient inventoryServiceClient;
    
    @GetMapping("/my-inventory")
    public ResponseEntity<String> getMyInventory(HttpServletRequest request,
                                               @RequestHeader("Authorization") String authHeader) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            String token = authHeader.substring(7); // Remove "Bearer " prefix
            
            // For restaurants, userId typically maps to restaurantId
            return inventoryServiceClient.getInventory(token, userId);
            
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("{\"success\": false, \"message\": \"Error retrieving inventory: " + e.getMessage() + "\"}");
        }
    }
    
    @PostMapping
    public ResponseEntity<String> addInventoryItem(HttpServletRequest request,
                                                 @RequestHeader("Authorization") String authHeader,
                                                 @RequestBody Object inventoryRequest) {
        try {
            String token = authHeader.substring(7); // Remove "Bearer " prefix
            
            return inventoryServiceClient.addInventoryItem(token, inventoryRequest);
            
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("{\"success\": false, \"message\": \"Error adding inventory item: " + e.getMessage() + "\"}");
        }
    }
    
    @PutMapping("/{itemId}")
    public ResponseEntity<String> updateInventoryItem(HttpServletRequest request,
                                                    @RequestHeader("Authorization") String authHeader,
                                                    @PathVariable Long itemId,
                                                    @RequestBody Object inventoryRequest) {
        try {
            String token = authHeader.substring(7); // Remove "Bearer " prefix
            
            return inventoryServiceClient.updateInventoryItem(token, itemId, inventoryRequest);
            
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("{\"success\": false, \"message\": \"Error updating inventory item: " + e.getMessage() + "\"}");
        }
    }
    
    @GetMapping("/**")
    public ResponseEntity<String> proxyGetRequest(HttpServletRequest request,
                                                @RequestHeader HttpHeaders headers) {
        try {
            String path = request.getRequestURI().replaceFirst("/api/inventory", "/api/inventory");
            return inventoryServiceClient.proxyRequest(path, HttpMethod.GET, headers, null);
            
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
            String path = request.getRequestURI().replaceFirst("/api/inventory", "/api/inventory");
            return inventoryServiceClient.proxyRequest(path, HttpMethod.POST, headers, body);
            
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
            String path = request.getRequestURI().replaceFirst("/api/inventory", "/api/inventory");
            return inventoryServiceClient.proxyRequest(path, HttpMethod.PUT, headers, body);
            
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("{\"success\": false, \"message\": \"Error processing request: " + e.getMessage() + "\"}");
        }
    }
    
    @DeleteMapping("/**")
    public ResponseEntity<String> proxyDeleteRequest(HttpServletRequest request,
                                                   @RequestHeader HttpHeaders headers) {
        try {
            String path = request.getRequestURI().replaceFirst("/api/inventory", "/api/inventory");
            return inventoryServiceClient.proxyRequest(path, HttpMethod.DELETE, headers, null);
            
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("{\"success\": false, \"message\": \"Error processing request: " + e.getMessage() + "\"}");
        }
    }
}
