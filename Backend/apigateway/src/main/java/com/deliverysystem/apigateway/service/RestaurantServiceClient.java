package com.deliverysystem.apigateway.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RestaurantServiceClient {
    
    private final RestTemplate restTemplate;
    @Value("${restaurant.service.url:http://localhost:9093}")
    private String restaurantServiceUrl;

    public RestaurantServiceClient() {
        this.restTemplate = new RestTemplate();
    }
    
    public ResponseEntity<String> proxyRequest(String path, HttpMethod method, 
                                             HttpHeaders headers, Object body) {
        String url = restaurantServiceUrl + path;
        
        HttpEntity<Object> request = new HttpEntity<>(body, headers);
        
        try {
            return restTemplate.exchange(url, method, request, String.class);
        } catch (Exception e) {
            throw new RuntimeException("Inventory service unavailable", e);
        }
    }
    
    public ResponseEntity<String> getRestaurant(String token, Long restaurantId) {
        String url = restaurantServiceUrl + "/api/restaurants/" + restaurantId;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            return restTemplate.exchange(url, HttpMethod.GET, request, String.class);
        } catch (Exception e) {
            throw new RuntimeException("Inventory service unavailable", e);
        }
    }
    
    public ResponseEntity<String> getRestaurants(String token) {
        String url = restaurantServiceUrl + "/api/restaurants";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            return restTemplate.exchange(url, HttpMethod.GET, request, String.class);
        } catch (Exception e) {
            throw new RuntimeException("Restaurant service unavailable", e);
        }
    }

    public ResponseEntity<String> addRestaurant(String token, Object restaurantRequest) {
        String url = restaurantServiceUrl + "/api/restaurants";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.set("Content-Type", "application/json");

        HttpEntity<Object> request = new HttpEntity<>(restaurantRequest, headers);
        
        try {
            return restTemplate.postForEntity(url, request, String.class);
        } catch (Exception e) {
            throw new RuntimeException("Inventory service unavailable", e);
        }
    }

    public ResponseEntity<String> updateRestaurant(String token, Long restaurantId, Object restaurantRequest) {
        String url = restaurantServiceUrl + "/api/restaurants/" + restaurantId;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.set("Content-Type", "application/json");

        HttpEntity<Object> request = new HttpEntity<>(restaurantRequest, headers);

        try {
            return restTemplate.exchange(url, HttpMethod.PUT, request, String.class);
        } catch (Exception e) {
            throw new RuntimeException("Inventory service unavailable", e);
        }
    }
    
    public ResponseEntity<String> deleteRestaurant(String token, Long restaurantId) {
        String url = restaurantServiceUrl + "/api/restaurants/" + restaurantId;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            return restTemplate.exchange(url, HttpMethod.DELETE, request, String.class);
        } catch (Exception e) {
            throw new RuntimeException("Restaurant service unavailable", e);
        }
    }
}
