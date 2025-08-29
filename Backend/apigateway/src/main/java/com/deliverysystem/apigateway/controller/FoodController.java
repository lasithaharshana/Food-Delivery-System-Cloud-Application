package com.deliverysystem.apigateway.controller;

import com.deliverysystem.apigateway.service.ProxyService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/foods")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('RESTAURANT')")
public class FoodController {
    
    @Autowired
    private ProxyService proxyService;
    @RequestMapping(value = "/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH})
    public ResponseEntity<String> proxyFoodService(HttpServletRequest request, @RequestBody(required = false) String body) {
        return proxyService.proxyRequest(request, body, "foods");
    }
}
