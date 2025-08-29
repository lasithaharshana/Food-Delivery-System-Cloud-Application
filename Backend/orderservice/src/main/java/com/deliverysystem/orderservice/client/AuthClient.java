package com.deliverysystem.orderservice.client;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.deliverysystem.orderservice.config.FeignConfig;


@FeignClient(name = "authservice", url = "http://apigateway:8080", configuration = FeignConfig.class)
public interface AuthClient {

    @GetMapping("/api/auth/userinfo")
    Object getUserInfo();

    @GetMapping("/api/users/check-restaurant/{restaurantId}")
    Map<String, Boolean> checkRestaurantExists(@PathVariable("restaurantId") Long restaurantId);

}
