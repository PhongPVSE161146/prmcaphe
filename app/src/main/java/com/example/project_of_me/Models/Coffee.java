package com.example.project_of_me.Models;

public class Coffee {
    private int productID;
    private String productName;
    private String briefDescription;
    private String fullDescription;
    private String technicalSpecifications;
    private double price;
    private String imageURL;
    private int categoryID;
    private String type; // dùng để lưu categoryName

    public Coffee() {
    }

    public Coffee(int productID, String productName, String briefDescription, String fullDescription, String technicalSpecifications, double price, String imageURL, int categoryID) {
        this.productID = productID;
        this.productName = productName;
        this.briefDescription = briefDescription;
        this.fullDescription = fullDescription;
        this.technicalSpecifications = technicalSpecifications;
        this.price = price;
        this.imageURL = imageURL;
        this.categoryID = categoryID;
    }

    public Coffee(String productName, String briefDescription, String fullDescription,
                  String technicalSpecifications, double price, String imageURL, int categoryID) {
        this.productName = productName;
        this.briefDescription = briefDescription;
        this.fullDescription = fullDescription;
        this.technicalSpecifications = technicalSpecifications;
        this.price = price;
        this.imageURL = imageURL;
        this.categoryID = categoryID;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getBriefDescription() {
        return briefDescription;
    }

    public void setBriefDescription(String briefDescription) {
        this.briefDescription = briefDescription;
    }

    public String getFullDescription() {
        return fullDescription;
    }

    public void setFullDescription(String fullDescription) {
        this.fullDescription = fullDescription;
    }

    public String getTechnicalSpecifications() {
        return technicalSpecifications;
    }

    public void setTechnicalSpecifications(String technicalSpecifications) {
        this.technicalSpecifications = technicalSpecifications;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
