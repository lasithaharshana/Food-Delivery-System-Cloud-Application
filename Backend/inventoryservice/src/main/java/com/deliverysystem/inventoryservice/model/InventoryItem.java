package com.deliverysystem.inventoryservice.model;

import jakarta.persistence.*;

@Entity
@Table(name = "inventory_items")
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private Integer qty;
    private String description;

    public InventoryItem() {
    }

    public InventoryItem(String name, Integer qty, String description) {
        this.name = name;
        this.qty = qty;
        this.description = description;
    }

    // Getters & setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getQty() { return qty; }
    public void setQty(Integer qty) { this.qty = qty; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
