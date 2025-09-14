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
        this.id = id;       // ID của giỏ hàng
        this.userId = userId;// ID của người dùng sở hữu giỏ hàng
        this.totalPrice = totalPrice;// tổng giá trị của giỏ hàng
        this.status = status; // "pending", "completed", "cancelled"
        this.createdAt = createdAt; // định dạng "yyyy-MM-dd HH:mm:ss"
    }

    // Getters và setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; } // ID của giỏ hàng

    public int getUserId() { return userId; } // ID của người dùng sở hữu giỏ hàng
    public void setUserId(int userId) { this.userId = userId; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
