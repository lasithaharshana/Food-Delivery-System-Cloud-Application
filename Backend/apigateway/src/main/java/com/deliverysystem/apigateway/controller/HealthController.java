package com.deliverysystem.apigateway.controller;

import com.deliverysystem.apigateway.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class HealthController {
    
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, Object>>> healthCheck() {
        Map<String, Object> healthData = new HashMap<>();
        healthData.put("status", "UP");
        healthData.put("service", "API Gateway");
        healthData.put("timestamp", LocalDateTime.now());
        healthData.put("version", "1.0.0");
        
        return ResponseEntity.ok(ApiResponse.success("API Gateway is running", healthData));
    }
    
    @GetMapping("/info")
    public ResponseEntity<ApiResponse<Map<String, Object>>> info() {
        Map<String, Object> infoData = new HashMap<>();
        infoData.put("service", "Food Delivery System API Gateway");
        infoData.put("description", "Central gateway routing requests to auth, order, and restaurant services");
        infoData.put("version", "1.0.0");
        infoData.put("features", new String[]{
            "JWT Authentication",
            "Role-based Authorization", 
            "Service Routing",
            "CORS Support",
            "Request Proxying"
        });
        
        return ResponseEntity.ok(ApiResponse.success(infoData));
    }
}
