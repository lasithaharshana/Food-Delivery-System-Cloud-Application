package com.deliverysystem.restaurantservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI inventoryServiceOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:9093");
        devServer.setDescription("Server URL for Inventory Service in Development environment");

        Contact contact = new Contact();
        contact.setEmail("developer@fooddelivery.com");
        contact.setName("Food Delivery Team");
        contact.setUrl("https://www.fooddelivery.com");

        License license = new License()
                .name("MIT License")
                .url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("Inventory Service API")
                .version("1.0.0")
                .contact(contact)
                .description("This API provides endpoints for inventory management including food items, stock levels, and availability tracking.")
                .termsOfService("https://www.fooddelivery.com/terms")
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer));
    }
}
