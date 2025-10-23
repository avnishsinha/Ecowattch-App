package com.example.ecowattchtechdemo.theme;

import androidx.annotation.ColorInt;

/**
 * Defines a complete color palette for the EcoWattch app.
 * Each palette contains all colors needed throughout the UI.
 */
public class ColorPalette {
    public final String name;

    // Accent color - used for headers, key metrics, emphasis
    @ColorInt
    public final int accentColor;

    // Accent color for meter fill (75-100% usage)
    @ColorInt
    public final int accentColorDark;

    // Primary background colors
    @ColorInt
    public final int backgroundDark;

    @ColorInt
    public final int containerBase;

    @ColorInt
    public final int containerLight;

    // Text colors (can vary per palette)
    @ColorInt
    public final int textPrimary;

    @ColorInt
    public final int textSecondary;

    // Optional: Secondary accent for variety (if needed)
    @ColorInt
    public final int accentSecondary;

    // Meter gradient colors (from low to high usage)
    @ColorInt
    public final int meterLow;      // 0-50% usage

    @ColorInt
    public final int meterMid;      // 50-75% usage

    @ColorInt
    public final int meterHigh;     // 75-100% usage

    // Bottom accent gradient (visible at bottom of screen)
    @ColorInt
    public final int gradientLight;

    @ColorInt
    public final int gradientDark;

    // Shop circle gradient (for palette preview circles)
    @ColorInt
    public final int circleGradientLight;

    @ColorInt
    public final int circleGradientDark;

    // UI Element Colors
    @ColorInt
    public final int screenBackgroundColor;     // Main screen background color

    @ColorInt
    public final int gradientCircleLight;       // Gradient circle light color

    @ColorInt
    public final int gradientCircleDark;        // Gradient circle dark color

    public ColorPalette(String name,
                       @ColorInt int accentColor,
                       @ColorInt int accentColorDark,
                       @ColorInt int backgroundDark,
                       @ColorInt int containerBase,
                       @ColorInt int containerLight,
                       @ColorInt int textPrimary,
                       @ColorInt int textSecondary,
                       @ColorInt int accentSecondary,
                       @ColorInt int meterLow,
                       @ColorInt int meterMid,
                       @ColorInt int meterHigh,
                       @ColorInt int gradientLight,
                       @ColorInt int gradientDark,
                       @ColorInt int circleGradientLight,
                       @ColorInt int circleGradientDark,
                       @ColorInt int screenBackgroundColor,
                       @ColorInt int gradientCircleLight,
                       @ColorInt int gradientCircleDark) {
        this.name = name;
        this.accentColor = accentColor;
        this.accentColorDark = accentColorDark;
        this.backgroundDark = backgroundDark;
        this.containerBase = containerBase;
        this.containerLight = containerLight;
        this.textPrimary = textPrimary;
        this.textSecondary = textSecondary;
        this.accentSecondary = accentSecondary;
        this.meterLow = meterLow;
        this.meterMid = meterMid;
        this.meterHigh = meterHigh;
        this.gradientLight = gradientLight;
        this.gradientDark = gradientDark;
        this.circleGradientLight = circleGradientLight;
        this.circleGradientDark = circleGradientDark;
        this.screenBackgroundColor = screenBackgroundColor;
        this.gradientCircleLight = gradientCircleLight;
        this.gradientCircleDark = gradientCircleDark;
    }
}
