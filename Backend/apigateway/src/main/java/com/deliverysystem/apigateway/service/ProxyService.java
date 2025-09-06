package com.deliverysystem.apigateway.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.core.io.ByteArrayResource;

import java.util.Enumeration;
import java.util.Map;


@Service
public class ProxyService {
    
    private final RestTemplate restTemplate;
    
    @Value("${auth.service.url}")
    private String authServiceUrl;
    
    @Value("${order.service.url}")
    private String orderServiceUrl;

    
    @Value("${food.service.url}")
    private String foodServiceUrl;

    public ProxyService() {
        this.restTemplate = new RestTemplate();
    }
    
    
    public ResponseEntity<String> proxyRequest(HttpServletRequest request, String body, String serviceType) {
        try {
            String targetServiceUrl = getServiceUrl(serviceType);

            String targetUrl = buildTargetUrl(targetServiceUrl, request);

            HttpHeaders headers = copyHeaders(request);

            HttpEntity<String> entity = new HttpEntity<>(body, headers);

            HttpMethod method = HttpMethod.valueOf(request.getMethod());

            ResponseEntity<String> response = restTemplate.exchange(targetUrl, method, entity, String.class);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.putAll(response.getHeaders());
            return new ResponseEntity<>(response.getBody(), responseHeaders, response.getStatusCode());

        } catch (org.springframework.web.client.HttpStatusCodeException ex) {
            HttpHeaders errorHeaders = new HttpHeaders();
            errorHeaders.putAll(ex.getResponseHeaders());
            return new ResponseEntity<>(ex.getResponseBodyAsString(), errorHeaders, ex.getStatusCode());
        } catch (Exception e) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            return new ResponseEntity<>(
                "{\"error\": \"Service unavailable\", \"message\": \"" + e.getMessage() + "\"}",
                headers,
                HttpStatus.SERVICE_UNAVAILABLE
            );
        }
    }
    
    // Handle file upload for multipart data
    public ResponseEntity<Map<String, String>> proxyFileUpload(MultipartFile file, String serviceType) {
        try {
            String targetServiceUrl = getServiceUrl(serviceType);
            String targetUrl = targetServiceUrl + "/api/foods/upload";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            
            // Create multipart body
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            });
            
            HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);
            
            // Use ParameterizedTypeReference for proper generic type handling
            ParameterizedTypeReference<Map<String, String>> responseType = new ParameterizedTypeReference<Map<String, String>>() {};
            ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                targetUrl, 
                HttpMethod.POST, 
                entity, 
                responseType
            );
            
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.putAll(response.getHeaders());
            return new ResponseEntity<>(response.getBody(), responseHeaders, response.getStatusCode());
            
        } catch (org.springframework.web.client.HttpStatusCodeException ex) {
            HttpHeaders errorHeaders = new HttpHeaders();
            errorHeaders.putAll(ex.getResponseHeaders());
            Map<String, String> errorBody = Map.of("error", ex.getResponseBodyAsString());
            return new ResponseEntity<>(errorBody, errorHeaders, ex.getStatusCode());
        } catch (Exception e) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, String> errorBody = Map.of("error", "File upload failed", "message", e.getMessage());
            return new ResponseEntity<>(errorBody, headers, HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
    
    
    private String getServiceUrl(String serviceType) {
        return switch (serviceType.toLowerCase()) {
            case "auth" -> authServiceUrl;
            case "order" -> orderServiceUrl;
            case "foods" -> foodServiceUrl;
            default -> throw new IllegalArgumentException("Unknown service type: " + serviceType);
        };
    }
    
    
    private String buildTargetUrl(String serviceUrl, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String queryString = request.getQueryString();

        String servicePath = requestUri;

        String targetUrl = serviceUrl + servicePath;

        if (queryString != null && !queryString.isEmpty()) {
            targetUrl += "?" + queryString;
        }

        return targetUrl;
    }
    
   
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
