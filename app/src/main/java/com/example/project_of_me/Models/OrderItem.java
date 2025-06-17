package com.example.project_of_me.Models;

public class OrderItem {
    private int orderItemId;
    private int orderId;
    private int foodId;
    private int quantity;
    private double price;

    public OrderItem(int orderItemId, int orderId, int foodId, int quantity, double price) {
        this.orderItemId = orderItemId;
        this.orderId = orderId;
        this.foodId = foodId;
        this.quantity = quantity;
        this.price = price;
    }

    public int getOrderItemId() {
        return orderItemId;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getFoodId() {
        return foodId;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setOrderItemId(int orderItemId) {
        this.orderItemId = orderItemId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(double price) {
        this.price = price;
    }

}
