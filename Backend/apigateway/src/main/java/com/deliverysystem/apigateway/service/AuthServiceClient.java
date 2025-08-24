package com.deliverysystem.apigateway.service;

import com.deliverysystem.apigateway.dto.AuthRequest;
import com.deliverysystem.apigateway.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthServiceClient {
    
    private final RestTemplate restTemplate;
    
    @Value("${auth.service.url}")
    private String authServiceUrl;
    
    public AuthServiceClient() {
        this.restTemplate = new RestTemplate();
    }
    
    public ResponseEntity<String> login(AuthRequest authRequest) {
        String url = authServiceUrl + "/api/auth/login";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        // Create the request body in the format expected by auth service
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("usernameOrEmail", authRequest.getUsernameOrEmail());
        requestBody.put("password", authRequest.getPassword());
        
        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
        
        try {
            return restTemplate.postForEntity(url, request, String.class);
        } catch (Exception e) {
            throw new RuntimeException("Authentication service unavailable", e);
        }
    }
    
    public ResponseEntity<String> register(RegisterRequest registerRequest) {
        String url = authServiceUrl + "/api/auth/register";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<RegisterRequest> request = new HttpEntity<>(registerRequest, headers);
        
        try {
            return restTemplate.postForEntity(url, request, String.class);
        } catch (Exception e) {
            throw new RuntimeException("Authentication service unavailable", e);
        }
    }
    
    public ResponseEntity<String> validateToken(String token) {
        String url = authServiceUrl + "/api/auth/validate";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        
        HttpEntity<String> request = new HttpEntity<>(headers);
        
        try {
            return restTemplate.exchange(url, HttpMethod.GET, request, String.class);
        } catch (Exception e) {
            throw new RuntimeException("Token validation failed", e);
        }
    }
}
