package com.example.ecowattchtechdemo;

public class ShopItem {
    private String name;
    private int price;
    private String description;

    public ShopItem(String name, int price, String description) {
        this.name = name;
        this.price = price;
        this.description = description;
    }

    // Getters
    public String getName() { return name; }
    public int getPrice() { return price; }
    public String getDescription() { return description; }
}
