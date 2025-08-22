package com.uuhnaut69.customerservice.service;

import com.uuhnaut69.customerservice.dto.AuthRequest;
import com.uuhnaut69.customerservice.dto.CustomerRequest;
import com.uuhnaut69.customerservice.model.Customer;
import com.uuhnaut69.customerservice.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository repository;
    private final PasswordEncoder passwordEncoder;

    public Customer create(CustomerRequest req) {
        repository.findByEmail(req.getEmail()).ifPresent(c -> {
            throw new IllegalArgumentException("email already in use");
        });

        Customer c = Customer.builder()
                .id(UUID.randomUUID())
                .name(req.getName())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .createdAt(OffsetDateTime.now())
                .build();
        return repository.save(c);
    }

    public Customer findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("customer not found"));
    }

    public List<Customer> findAll() {
        return repository.findAll();
    }

    public Customer update(UUID id, CustomerRequest req) {
        Customer c = findById(id);
        c.setName(req.getName());
        c.setEmail(req.getEmail());
        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            c.setPassword(passwordEncoder.encode(req.getPassword()));
        }
        return repository.save(c);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public String authenticate(AuthRequest req) {
        Customer c = repository.findByEmail(req.getEmail()).orElseThrow(() -> new IllegalArgumentException("invalid credentials"));
        if (!passwordEncoder.matches(req.getPassword(), c.getPassword())) {
            throw new IllegalArgumentException("invalid credentials");
        }
        // simple token for demo; replace with JWT in production
        return "token:" + UUID.randomUUID();
    }
}
