package com.deliverysystem.foodservice.client;
import java.util.Map;
import com.deliverysystem.foodservice.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "authservice",
    url = "http://apigateway:8080",
    configuration = FeignConfig.class
)
public interface AuthClient {

    @GetMapping("/api/auth/userinfo")
    Object getUserInfo();

    @GetMapping("/api/users/check-restaurant/{restaurantId}")
    Map<String, Object> checkRestaurantExists(@PathVariable("restaurantId") String restaurantId);

}
