package com.deliverysystem.apigateway.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Enumeration;

/**
 * Generic proxy service that forwards requests to appropriate microservices
 * without modifying the responses. Preserves all headers and request details.
 */
@Service
public class ProxyService {
    
    private final RestTemplate restTemplate;
    
    @Value("${auth.service.url}")
    private String authServiceUrl;
    
    @Value("${order.service.url}")
    private String orderServiceUrl;
    
    @Value("${inventory.service.url}")
    private String inventoryServiceUrl;
    
    public ProxyService() {
        this.restTemplate = new RestTemplate();
    }
    
    /**
     * Proxy a request to the appropriate service without modifying the response
     * 
     * @param request The original HTTP request
     * @param body The request body (if any)
     * @param serviceType The target service type ("auth", "order", "inventory")
     * @return The exact response from the target service
     */
    public ResponseEntity<String> proxyRequest(HttpServletRequest request, String body, String serviceType) {
        try {
            // Determine target service URL
            String targetServiceUrl = getServiceUrl(serviceType);
            
            // Build the target URL
            String targetUrl = buildTargetUrl(targetServiceUrl, request);
            
            // Copy headers from original request
            HttpHeaders headers = copyHeaders(request);
            
            // Create HTTP entity with body and headers
            HttpEntity<String> entity = new HttpEntity<>(body, headers);
            
            // Get HTTP method
            HttpMethod method = HttpMethod.valueOf(request.getMethod());
            
            // Forward the request and return the exact response
            return restTemplate.exchange(targetUrl, method, entity, String.class);
            
        } catch (Exception e) {
            // Return service error in JSON format
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            return new ResponseEntity<>(
                "{\"error\": \"Service unavailable\", \"message\": \"" + e.getMessage() + "\"}", 
                headers, 
                HttpStatus.SERVICE_UNAVAILABLE
            );
        }
    }
    
    /**
     * Get the service URL based on service type
     */
    private String getServiceUrl(String serviceType) {
        return switch (serviceType.toLowerCase()) {
            case "auth" -> authServiceUrl;
            case "order" -> orderServiceUrl;
            case "inventory" -> inventoryServiceUrl;
            default -> throw new IllegalArgumentException("Unknown service type: " + serviceType);
        };
    }
    
    /**
     * Build the target URL by replacing the gateway prefix with the service prefix
     */
    private String buildTargetUrl(String serviceUrl, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String queryString = request.getQueryString();
        
        // Transform paths to match service endpoints
        String servicePath;
        if (requestUri.startsWith("/api/orders")) {
            // Transform /api/orders to /api/order for order service (handles both /api/orders and /api/orders/*)
            servicePath = requestUri.replace("/api/orders", "/api/order");
        } else {
            // Keep the full path for auth and inventory services
            servicePath = requestUri;
        }
        
        String targetUrl = serviceUrl + servicePath;
        
        if (queryString != null && !queryString.isEmpty()) {
            targetUrl += "?" + queryString;
        }
        
        return targetUrl;
    }
    
    /**
     * Copy relevant headers from the original request
     */
    private HttpHeaders copyHeaders(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        
        // Copy all headers except host and content-length (Spring will set these)
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            
            // Skip headers that should not be forwarded
            if (shouldForwardHeader(headerName)) {
                Enumeration<String> headerValues = request.getHeaders(headerName);
                while (headerValues.hasMoreElements()) {
                    String headerValue = headerValues.nextElement();
                    headers.add(headerName, headerValue);
                }
            }
        }
        
        return headers;
    }
    
    /**
     * Determine if a header should be forwarded to the target service
     */
    private boolean shouldForwardHeader(String headerName) {
        String lowerHeaderName = headerName.toLowerCase();
        
        // Don't forward these headers as they are connection-specific
        return !lowerHeaderName.equals("host") &&
               !lowerHeaderName.equals("content-length") &&
               !lowerHeaderName.equals("transfer-encoding") &&
               !lowerHeaderName.equals("connection") &&
               !lowerHeaderName.equals("upgrade") &&
               !lowerHeaderName.equals("proxy-connection") &&
               !lowerHeaderName.equals("proxy-authenticate") &&
               !lowerHeaderName.equals("proxy-authorization") &&
               !lowerHeaderName.equals("te") &&
               !lowerHeaderName.equals("trailers");
    }
}
