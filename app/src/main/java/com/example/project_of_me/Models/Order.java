package com.example.project_of_me.Models;

public class Order {
    private int orderId;
    private int userId;
    private double totalPrice;
    private String orderDate;
    private String orderStatus;

    public Order(int orderId, int userId, double totalPrice, String orderDate, String orderStatus) {
        this.orderId = orderId;
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getUserId() {
        return userId;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
