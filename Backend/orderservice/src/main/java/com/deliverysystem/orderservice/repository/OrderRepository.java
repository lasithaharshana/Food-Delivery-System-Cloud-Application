package com.deliverysystem.orderservice.repository;
import com.deliverysystem.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    // Additional query methods can be defined here if needed
    
}

