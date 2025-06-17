package com.example.project_of_me.Models;

public class CartItem {
    private int id;
    private int orderId;
    private int coffeeId;
    private int quantity;
    private double price;

    public CartItem() {
    }

    public CartItem(int id, int orderId, int coffeeId, int quantity, double price) {
        this.id = id;
        this.orderId = orderId;
        this.coffeeId = coffeeId;
        this.quantity = quantity;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getCoffeeId() {
        return coffeeId;
    }

    public void setCoffeeId(int coffeeId) {
        this.coffeeId = coffeeId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
