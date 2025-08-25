package com.deliverysystem.apigateway.controller;

import com.deliverysystem.apigateway.service.ProxyService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private ProxyService proxyService;
    
    /**
     * Route all user management requests to auth service
     * JWT validation ensures only authenticated users can access
     */
    @RequestMapping(value = "/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH})
    public ResponseEntity<String> proxyUserService(HttpServletRequest request, @RequestBody(required = false) String body) {
        return proxyService.proxyRequest(request, body, "auth");
    }
}
