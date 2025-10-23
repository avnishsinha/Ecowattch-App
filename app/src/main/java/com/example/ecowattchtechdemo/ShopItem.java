package com.example.ecowattchtechdemo;

import androidx.annotation.ColorInt;

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
    private String paletteId;  // ID for palette items (PEACH, LIME, FROST)
    @ColorInt
    private int accentColor;   // Main accent color for palettes
    @ColorInt
    private int circleGradientLight;  // Light color for gradient circle
    @ColorInt
    private int circleGradientDark;   // Dark color for gradient circle

    // Constructor for palette items with color and gradient
    public ShopItem(String name, int price, int colorResource, String paletteId, @ColorInt int accentColor,
                    @ColorInt int gradientLight, @ColorInt int gradientDark) {
        this.name = name;
        this.price = price;
        this.colorResource = colorResource;
        this.paletteId = paletteId;
        this.accentColor = accentColor;
        this.circleGradientLight = gradientLight;
        this.circleGradientDark = gradientDark;
        this.isOwned = false;
        this.isSelected = false;
    }

    // Constructor for palette items with color only (backward compatibility)
    public ShopItem(String name, int price, int colorResource, String paletteId, @ColorInt int accentColor) {
        this.name = name;
        this.price = price;
        this.colorResource = colorResource;
        this.paletteId = paletteId;
        this.accentColor = accentColor;
        this.circleGradientLight = accentColor;
        this.circleGradientDark = accentColor;
        this.isOwned = false;
        this.isSelected = false;
    }

    // Legacy constructor for backward compatibility
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

    public String getPaletteId() {
        return paletteId;
    }

    public int getAccentColor() {
        return accentColor;
    }

    public int getCircleGradientLight() {
        return circleGradientLight;
    }

    public int getCircleGradientDark() {
        return circleGradientDark;
    }
}
