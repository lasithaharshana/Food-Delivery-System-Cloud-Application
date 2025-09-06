package com.deliverysystem.apigateway.controller;

import com.deliverysystem.apigateway.service.ProxyService;
import jakarta.servlet.http.HttpServletRequest;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/foods")
@CrossOrigin(origins = "*")
public class FoodController {
    
    @Autowired
    private ProxyService proxyService;

    // Handle file upload separately to support multipart data
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        return proxyService.proxyFileUpload(file, "foods");
    }
    
    @RequestMapping(value = "/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH}, 
                    consumes = {"!multipart/form-data"})
    public ResponseEntity<String> proxyFoodService(HttpServletRequest request, @RequestBody(required = false) byte[] body) {
        return proxyService.proxyRequest(request, body == null ? null : new String(body, StandardCharsets.UTF_8), "foods");
    }
}
