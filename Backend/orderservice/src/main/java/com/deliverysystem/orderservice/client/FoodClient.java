package com.deliverysystem.orderservice.client;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.deliverysystem.orderservice.config.FeignConfig;

@FeignClient(name = "foodservice", url = "http://apigateway:8080", configuration = FeignConfig.class)
public interface FoodClient {

    @GetMapping("/api/foods/{foodId}/exists")
    boolean checkFoodExists(@PathVariable("foodId") Long foodId);

    @PostMapping("/api/foods/exists")
    Map<Long, Boolean> checkFoodsExist(@RequestBody List<Long> foodIds);

    @PostMapping("/api/foods/reduce-stock")
    ResponseEntity<String> reduceStock(@RequestBody Map<String, Integer> foodQuantities);
}
