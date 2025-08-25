package com.deliverysystem.apigateway.controller;

import com.deliverysystem.apigateway.service.ProxyService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/restaurants")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('RESTAURANT')")
public class RestaurantController {
    
    @Autowired
    private ProxyService proxyService;
    
    /**
     * Route all inventory requests to inventory service
     * JWT validation ensures only RESTAURANT role can access
     */
    @RequestMapping(value = "/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH})
    public ResponseEntity<String> proxyInventoryService(HttpServletRequest request, @RequestBody(required = false) String body) {
        return proxyService.proxyRequest(request, body, "inventory");
    }
}
