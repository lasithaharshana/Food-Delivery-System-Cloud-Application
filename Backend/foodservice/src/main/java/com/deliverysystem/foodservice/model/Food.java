package com.deliverysystem.foodservice.model;
import jakarta.persistence.*;

@Entity
@Table(name = "foods")
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = false, nullable = false)
    private Long restaurantId;

    private String name;
    private String description;
    private Double price;
    private Double quantity;
    private String category;
    private String imageUrl;

    @Column(nullable = false)
    private String status = "available"; 

    private boolean popular;

    public Food() {
        this.status = "available";
    }

    public Food(Long restaurantId, String name, String description, Double price, Double quantity, String category,
                 String imageUrl, String status, boolean popular) {
        this.restaurantId = restaurantId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.imageUrl = imageUrl;
        setStatus(status); // use setter to validate
        this.popular = popular;
    }

    // Getters & setters
    public Long getId() { return id; }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
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

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
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
            this.status = "available";
        } else if (!status.equalsIgnoreCase("available") && !status.equalsIgnoreCase("unavailable")) {
            throw new IllegalArgumentException("Status must be 'available' or 'unavailable'");
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
