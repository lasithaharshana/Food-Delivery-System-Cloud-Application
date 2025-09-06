package com.deliverysystem.apigateway.config;

import com.deliverysystem.apigateway.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Value("${cors.allowed.origins:${CORS_ALLOWED_ORIGINS:http://localhost:3000,http://localhost:5173,http://localhost:8080}}")
    private String allowedOrigins;
    
    @Value("${cors.allowed.methods:${CORS_ALLOWED_METHODS:GET,POST,PUT,DELETE,PATCH,OPTIONS}}")
    private String allowedMethods;
    
    @Value("${cors.allowed.headers:${CORS_ALLOWED_HEADERS:*}}")
    private String allowedHeaders;
    
    @Value("${cors.allow.credentials:${CORS_ALLOW_CREDENTIALS:true}}")
    private boolean allowCredentials;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                .requestMatchers("/api/health", "/api/info").permitAll()
                
                // File upload/access endpoints - no authentication required
                .requestMatchers("/api/foods/upload").permitAll()
                .requestMatchers("/api/foods/uploads/**").permitAll()
                
                // Protected endpoints
                .requestMatchers("/api/order/**").authenticated()
                .requestMatchers("/api/foods/**").authenticated()
                .requestMatchers("/api/user/**").authenticated()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
        configuration.setAllowedMethods(Arrays.asList(allowedMethods.split(",")));
        configuration.setAllowedHeaders(List.of(allowedHeaders));
        configuration.setAllowCredentials(allowCredentials);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
