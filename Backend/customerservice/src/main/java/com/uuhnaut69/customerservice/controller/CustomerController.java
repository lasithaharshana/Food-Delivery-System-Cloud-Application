package com.uuhnaut69.customerservice.controller;

import com.uuhnaut69.customerservice.dto.AuthRequest;
import com.uuhnaut69.customerservice.dto.CustomerRequest;
import com.uuhnaut69.customerservice.dto.CustomerResponse;
import com.uuhnaut69.customerservice.model.Customer;
import com.uuhnaut69.customerservice.service.CustomerService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponse create(@RequestBody @Valid CustomerRequest req) {
        Customer created = customerService.create(req);
        return toResponse(created);
    }

    @GetMapping
    public List<CustomerResponse> list() {
        return customerService.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public CustomerResponse get(@PathVariable UUID id) {
        return toResponse(customerService.findById(id));
    }

    @PutMapping("/{id}")
    public CustomerResponse update(@PathVariable UUID id, @RequestBody @Valid CustomerRequest req) {
        return toResponse(customerService.update(id, req));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        customerService.delete(id);
    }

    // Authentication endpoint kept inside the same service
    @PostMapping("/auth/login")
    public Map<String, String> login(@RequestBody AuthRequest req) {
        String token = customerService.authenticate(req);
        return Map.of("token", token);
    }

    private CustomerResponse toResponse(Customer c) {
        return CustomerResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .email(c.getEmail())
                .createdAt(c.getCreatedAt())
                .build();
    }
}
