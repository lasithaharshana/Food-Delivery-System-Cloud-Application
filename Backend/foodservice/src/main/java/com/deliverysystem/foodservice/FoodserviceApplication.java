package com.deliverysystem.foodservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.deliverysystem.foodservice.client")
public class FoodserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FoodserviceApplication.class, args);
	}

}
