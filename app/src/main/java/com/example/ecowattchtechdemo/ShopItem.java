package com.example.ecowattchtechdemo;

/**
 * Model class representing a shop item (palette, icon, etc.)
 * Backend can extend this with additional fields as needed
 */
public class ShopItem {
    private String name;
    private int price;
    private int colorResource;
    private boolean isOwned;
    private boolean isSelected;

    public ShopItem(String name, int price, int colorResource) {
        this.name = name;
        this.price = price;
        this.colorResource = colorResource;
        this.isOwned = false;
        this.isSelected = false;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getPriceText() {
        return price + " Energy";
    }

    public int getColorResource() {
        return colorResource;
    }

    public boolean isOwned() {
        return isOwned;
    }

    public void setOwned(boolean owned) {
        isOwned = owned;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
