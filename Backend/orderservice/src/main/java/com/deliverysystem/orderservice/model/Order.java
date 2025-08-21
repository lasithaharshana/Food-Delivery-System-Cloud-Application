package com.deliverysystem.orderservice.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private Integer qty;
    private String description;

    private String customerName;
    private String address;
    private LocalDate orderDate;
    private Double totalCost;

    public Order() {}

    public Order(String name, Integer qty, String description, String customerName, String address, LocalDate orderDate, Double totalCost) {
        this.name = name;
        this.qty = qty;
        this.description = description;
        this.customerName = customerName;
        this.address = address;
        this.orderDate = orderDate;
        this.totalCost = totalCost;
    }

    // Getters & setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }
}