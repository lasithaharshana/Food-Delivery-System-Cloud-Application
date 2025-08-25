package com.deliverysystem.apigateway.service;

import com.deliverysystem.apigateway.dto.AuthRequest;
import com.deliverysystem.apigateway.dto.RegisterRequest;
import com.deliverysystem.apigateway.dto.UpdateUserRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthServiceClient {
    
    private final RestTemplate restTemplate;
    
    @Value("${auth.service.url:http://localhost:8081}")
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
        
        // Create request body with token
        String requestBody = "{\"token\":\"" + token + "\"}";
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        
        try {
            return restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        } catch (Exception e) {
            throw new RuntimeException("Token validation failed", e);
        }
    }

    public ResponseEntity<String> validateTokenPost(String token) {
        String url = authServiceUrl + "/api/auth/validate";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        // Create request body with token
        String requestBody = "{\"token\":\"" + token + "\"}";
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        
        try {
            return restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        } catch (Exception e) {
            throw new RuntimeException("Token validation failed", e);
        }
    }
    
    // User Management Methods
    public ResponseEntity<String> getUserById(Long id, String token) {
        String url = authServiceUrl + "/api/users/" + id;
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        
        HttpEntity<String> request = new HttpEntity<>(headers);
        
        try {
            return restTemplate.exchange(url, HttpMethod.GET, request, String.class);
        } catch (HttpClientErrorException e) {
            // Forward the exact HTTP client error response (4xx)
            return ResponseEntity.status(e.getStatusCode())
                    .headers(e.getResponseHeaders())
                    .body(e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            // Forward the exact HTTP server error response (5xx)
            return ResponseEntity.status(e.getStatusCode())
                    .headers(e.getResponseHeaders())
                    .body(e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new RuntimeException("User service unavailable", e);
        }
    }
    
    public ResponseEntity<String> getUserByUsername(String username, String token) {
        String url = authServiceUrl + "/api/users/username/" + username;
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        
        HttpEntity<String> request = new HttpEntity<>(headers);
        
        try {
            return restTemplate.exchange(url, HttpMethod.GET, request, String.class);
        } catch (Exception e) {
            throw new RuntimeException("User service unavailable", e);
        }
    }
    
    public ResponseEntity<String> getUserByEmail(String email, String token) {
        String url = authServiceUrl + "/api/users/email/" + email;
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        
        HttpEntity<String> request = new HttpEntity<>(headers);
        
        try {
            return restTemplate.exchange(url, HttpMethod.GET, request, String.class);
        } catch (Exception e) {
            throw new RuntimeException("User service unavailable", e);
        }
    }
    
    public ResponseEntity<String> getAllUsers(String token, String queryParams) {
        String url = authServiceUrl + "/api/users" + (queryParams != null ? "?" + queryParams : "");
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        
        HttpEntity<String> request = new HttpEntity<>(headers);
        
        try {
            return restTemplate.exchange(url, HttpMethod.GET, request, String.class);
        } catch (Exception e) {
            throw new RuntimeException("User service unavailable", e);
        }
    }
    
    public ResponseEntity<String> getUsersByRole(String role, String token) {
        String url = authServiceUrl + "/api/users/role/" + role;
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        
        HttpEntity<String> request = new HttpEntity<>(headers);
        
        try {
            return restTemplate.exchange(url, HttpMethod.GET, request, String.class);
        } catch (Exception e) {
            throw new RuntimeException("User service unavailable", e);
        }
    }
    
    public ResponseEntity<String> getActiveUsers(String token) {
        String url = authServiceUrl + "/api/users/active";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        
        HttpEntity<String> request = new HttpEntity<>(headers);
        
        try {
            return restTemplate.exchange(url, HttpMethod.GET, request, String.class);
        } catch (Exception e) {
            throw new RuntimeException("User service unavailable", e);
        }
    }
    
    public ResponseEntity<String> getInactiveUsers(String token) {
        String url = authServiceUrl + "/api/users/inactive";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        
        HttpEntity<String> request = new HttpEntity<>(headers);
        
        try {
            return restTemplate.exchange(url, HttpMethod.GET, request, String.class);
        } catch (Exception e) {
            throw new RuntimeException("User service unavailable", e);
        }
    }
    
    public ResponseEntity<String> updateUser(Long id, UpdateUserRequest updateRequest, String token) {
        String url = authServiceUrl + "/api/users/" + id;
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        
        HttpEntity<UpdateUserRequest> request = new HttpEntity<>(updateRequest, headers);
        
        try {
            return restTemplate.exchange(url, HttpMethod.PUT, request, String.class);
        } catch (HttpClientErrorException e) {
            // Forward the exact HTTP client error response (4xx)
            return ResponseEntity.status(e.getStatusCode())
                    .headers(e.getResponseHeaders())
                    .body(e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            // Forward the exact HTTP server error response (5xx)
            return ResponseEntity.status(e.getStatusCode())
                    .headers(e.getResponseHeaders())
                    .body(e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new RuntimeException("User service unavailable", e);
        }
    }
    
    public ResponseEntity<String> deleteUser(Long id, String token) {
        String url = authServiceUrl + "/api/users/" + id;
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        
        HttpEntity<String> request = new HttpEntity<>(headers);
        
        try {
            return restTemplate.exchange(url, HttpMethod.DELETE, request, String.class);
        } catch (HttpClientErrorException e) {
            // Forward the exact HTTP client error response (4xx)
            return ResponseEntity.status(e.getStatusCode())
                    .headers(e.getResponseHeaders())
                    .body(e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            // Forward the exact HTTP server error response (5xx)
            return ResponseEntity.status(e.getStatusCode())
                    .headers(e.getResponseHeaders())
                    .body(e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new RuntimeException("User service unavailable", e);
        }
    }
    
    public ResponseEntity<String> deactivateUser(Long id, String token) {
        String url = authServiceUrl + "/api/users/" + id + "/deactivate";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        
        HttpEntity<String> request = new HttpEntity<>(headers);
        
        try {
            return restTemplate.exchange(url, HttpMethod.PATCH, request, String.class);
        } catch (Exception e) {
            throw new RuntimeException("User service unavailable", e);
        }
    }
    
    public ResponseEntity<String> activateUser(Long id, String token) {
        String url = authServiceUrl + "/api/users/" + id + "/activate";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        
        HttpEntity<String> request = new HttpEntity<>(headers);
        
        try {
            return restTemplate.exchange(url, HttpMethod.PATCH, request, String.class);
        } catch (Exception e) {
            throw new RuntimeException("User service unavailable", e);
        }
    }
}
