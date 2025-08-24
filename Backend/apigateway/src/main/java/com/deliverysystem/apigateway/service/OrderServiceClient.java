package com.deliverysystem.apigateway.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OrderServiceClient {
    
    private final RestTemplate restTemplate;
    
    @Value("${order.service.url}")
    private String orderServiceUrl;
    
    public OrderServiceClient() {
        this.restTemplate = new RestTemplate();
    }
    
    public ResponseEntity<String> proxyRequest(String path, HttpMethod method, 
                                             HttpHeaders headers, Object body) {
        String url = orderServiceUrl + path;
        
        HttpEntity<Object> request = new HttpEntity<>(body, headers);
        
        try {
            return restTemplate.exchange(url, method, request, String.class);
        } catch (Exception e) {
            throw new RuntimeException("Order service unavailable", e);
        }
    }
    
    public ResponseEntity<String> getOrders(String token, Long userId) {
        String url = orderServiceUrl + "/api/orders/user/" + userId;
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        
        HttpEntity<String> request = new HttpEntity<>(headers);
        
        try {
            return restTemplate.exchange(url, HttpMethod.GET, request, String.class);
        } catch (Exception e) {
            throw new RuntimeException("Order service unavailable", e);
        }
    }
    
    public ResponseEntity<String> createOrder(String token, Object orderRequest) {
        String url = orderServiceUrl + "/api/orders";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.set("Content-Type", "application/json");
        
        HttpEntity<Object> request = new HttpEntity<>(orderRequest, headers);
        
        try {
            return restTemplate.postForEntity(url, request, String.class);
        } catch (Exception e) {
            throw new RuntimeException("Order service unavailable", e);
        }
    }
}
