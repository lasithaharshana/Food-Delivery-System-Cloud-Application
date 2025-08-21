package com.deliverysystem.inventoryservice.controller;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {

    private static final Logger logger = LoggerFactory.getLogger(InventoryController.class);


    // Test endpoint
    @GetMapping("/test")
    public String testEndpoint() {
        logger.info("Test endpoint called");
        return "Inventory Service is running!";
    }
}
