package com.example.project_of_me.Models;

public class Cart {
    private int id;
    private int userId;
    private double totalPrice;
    private String status;
    private String createdAt; // dạng chuỗi, ví dụ "2025-03-18 14:00:00"

    public Cart() {
    }

    public Cart(int id, int userId, double totalPrice, String status, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters và setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
