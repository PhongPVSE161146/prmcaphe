package com.example.project_of_me.Models;

public class User {
    private int id;
    private String name; // Tên người dùng
    // Các trường khác như email, phone, password, address, role
    private String email;
    private String phone; // Số điện thoại người dùng
    private String password;
    private String address;
    private String role;

    public User() {
    }

    public User(int id, String name, String email, String phone, String password, String address,String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.address = address;
        this.role = role;
    }
    public User(String name, String email, String phone, String password, String address,String role) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.address = address;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String address) {
        this.role = role;
    }


}