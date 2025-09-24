package com.example.ecowattchtechdemo;

public class ShopItem {
    private String name;
    private int price;
    private boolean isSelected;

    public ShopItem(String name, int price, boolean isSelected) {
        this.name = name;
        this.price = price;
        this.isSelected = isSelected;
    }

    // Getters
    public String getName() { return name; }
    public int getPrice() { return price; }
    public boolean isSelected() { return isSelected; }
    
    // Setters
    public void setSelected(boolean selected) { this.isSelected = selected; }
}
