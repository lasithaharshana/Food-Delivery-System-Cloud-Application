package com.deliverysystem.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "restaurantservice", url = "http://localhost:9093")
public interface RestaurantClient {

    @GetMapping("/api/restaurants/{restaurantId}/exists")
    Boolean checkRestaurantExists(@PathVariable("restaurantId") String restaurantId);
}
