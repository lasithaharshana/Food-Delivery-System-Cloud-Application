package com.deliverysystem.apigateway.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class InventoryServiceClient {
    
    private final RestTemplate restTemplate;
    
    @Value("${inventory.service.url:http://localhost:9093}")
    private String inventoryServiceUrl;
    
    public InventoryServiceClient() {
        this.restTemplate = new RestTemplate();
    }
    
    public ResponseEntity<String> proxyRequest(String path, HttpMethod method, 
                                             HttpHeaders headers, Object body) {
        String url = inventoryServiceUrl + path;
        
        HttpEntity<Object> request = new HttpEntity<>(body, headers);
        
        try {
            return restTemplate.exchange(url, method, request, String.class);
        } catch (Exception e) {
            throw new RuntimeException("Inventory service unavailable", e);
        }
    }
    
    public ResponseEntity<String> getInventory(String token, Long restaurantId) {
        String url = inventoryServiceUrl + "/api/inventory/restaurant/" + restaurantId;
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        
        HttpEntity<String> request = new HttpEntity<>(headers);
        
        try {
            return restTemplate.exchange(url, HttpMethod.GET, request, String.class);
        } catch (Exception e) {
            throw new RuntimeException("Inventory service unavailable", e);
        }
    }
    
    public ResponseEntity<String> addInventoryItem(String token, Object inventoryRequest) {
        String url = inventoryServiceUrl + "/api/inventory";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.set("Content-Type", "application/json");
        
        HttpEntity<Object> request = new HttpEntity<>(inventoryRequest, headers);
        
        try {
            return restTemplate.postForEntity(url, request, String.class);
        } catch (Exception e) {
            throw new RuntimeException("Inventory service unavailable", e);
        }
    }
    
    public ResponseEntity<String> updateInventoryItem(String token, Long itemId, Object inventoryRequest) {
        String url = inventoryServiceUrl + "/api/inventory/" + itemId;
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.set("Content-Type", "application/json");
        
        HttpEntity<Object> request = new HttpEntity<>(inventoryRequest, headers);
        
        try {
            return restTemplate.exchange(url, HttpMethod.PUT, request, String.class);
        } catch (Exception e) {
            throw new RuntimeException("Inventory service unavailable", e);
        }
    }
}
