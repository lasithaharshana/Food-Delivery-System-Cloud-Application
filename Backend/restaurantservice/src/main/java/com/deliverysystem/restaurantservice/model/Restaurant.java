package com.deliverysystem.restaurantservice.model;

import java.util.UUID;

import jakarta.persistence.*;

@Entity
@Table(name = "restaurants")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String restaurantId;

    private String name;
    private String description;
    private Double price;
    private String category;
    private String imageUrl;

    @Column(nullable = false)
    private String status = "active"; 

    private boolean popular;

    public Restaurant() {
        this.status = "active";
    }

    public Restaurant(String name, String description, Double price, String category,
                      String imageUrl, String status, boolean popular) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.imageUrl = imageUrl;
        setStatus(status); // use setter to validate
        this.popular = popular;
    }

    @PrePersist
    public void generateRestaurantId() {
        if (restaurantId == null) {
            restaurantId = UUID.randomUUID().toString();
        }
        if (status == null) {
            status = "active";
        }
    }

    // Getters & setters
    public Integer getId() { return id; }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (status == null) {
            this.status = "active";
        } else if (!status.equalsIgnoreCase("active") && !status.equalsIgnoreCase("inactive")) {
            throw new IllegalArgumentException("Status must be 'active' or 'inactive'");
        } else {
            this.status = status.toLowerCase();
        }
    }

    public boolean getPopular() {
        return popular;
    }

    public void setPopular(boolean popular) {
        this.popular = popular;
    }
}
