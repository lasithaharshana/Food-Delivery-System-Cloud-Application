package com.deliverysystem.authservice.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class EnvironmentConfig {

    @PostConstruct
    public void loadEnvironmentVariables() {
        try {
            // Load .env file from the Backend directory (parent of authservice)
            Dotenv dotenv = Dotenv.configure()
                    .directory("../") // Go up one level from authservice to Backend
                    .ignoreIfMalformed()
                    .ignoreIfMissing()
                    .load();

            // Set system properties for Spring Boot to pick up
            dotenv.entries().forEach(entry -> {
                String key = entry.getKey();
                String value = entry.getValue();
                
                // Only set if not already defined as system property or environment variable
                if (System.getProperty(key) == null && System.getenv(key) == null) {
                    System.setProperty(key, value);
                }
            });
            
            log.info("Environment variables loaded successfully from .env file");
        } catch (Exception e) {
            log.warn("Could not load .env file: {}", e.getMessage());
        }
    }
}
