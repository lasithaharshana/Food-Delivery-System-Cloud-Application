package com.deliverysystem.foodservice.dto;

public class ReduceStockResponse {
    private String message;

    public ReduceStockResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
