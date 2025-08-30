package com.deliverysystem.orderservice.client;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.deliverysystem.orderservice.config.FeignConfig;
import com.deliverysystem.orderservice.dto.UserResponse;
import com.deliverysystem.orderservice.dto.ValidateTokenRequest;


@FeignClient(name = "authservice", url = "http://authservice:8081", configuration = FeignConfig.class)
public interface AuthClient {

    @PostMapping("/api/auth/validate")
    UserResponse validateToken(@RequestBody ValidateTokenRequest request);

    @GetMapping("/api/users/check-restaurant/{restaurantId}")
    Map<String, Boolean> checkRestaurantExists(@PathVariable("restaurantId") Long restaurantId);
}

