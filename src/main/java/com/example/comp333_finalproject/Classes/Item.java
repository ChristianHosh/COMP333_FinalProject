package com.example.comp333_finalproject.Classes;

public class Item {
    private int id;
    private String name;
    private String brand;
    private int quantity;
    private double price;
    private String color;
    private String imagePath;

    public Item(int id, String name, String brand, int quantity, double price, String color, String imagePath) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.quantity = quantity;
        this.price = price;
        this.color = color;
        this.imagePath = imagePath;
    }

    public Item(String name, String brand,  String color, double price) {
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.color = color;
    }

    // GETTERS & SETTERS
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

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
