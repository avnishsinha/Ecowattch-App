package com.example.ecowattchtechdemo.theme;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.ColorInt;
import java.util.ArrayList;
import java.util.List;

/**
 * Centralized theme manager for EcoWattch.
 * Singleton pattern ensures all activities share the same listener list.
 * Handles palette selection, persistence, and retrieval.
 * Agnostic to how colors are applied - just stores and returns them.
 * Supports theme change listeners for live theme updates across the app.
 */
public class ThemeManager {
    private static final String PREFS_NAME = "EcoWattchTheme";
    private static final String KEY_SELECTED_PALETTE = "selected_palette";
    private static final String DEFAULT_PALETTE = "PEACH";

    private static ThemeManager instance;
    private final SharedPreferences prefs;
    private ColorPalette currentPalette;
    private final List<ThemeChangeListener> themeChangeListeners = new ArrayList<>();

    private ThemeManager(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        loadCurrentPalette();
    }

    /**
     * Get the singleton instance of ThemeManager
     */
    public static synchronized ThemeManager getInstance(Context context) {
        if (instance == null) {
            instance = new ThemeManager(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Load the currently selected palette from preferences
     */
    private void loadCurrentPalette() {
        String paletteName = prefs.getString(KEY_SELECTED_PALETTE, DEFAULT_PALETTE);
        this.currentPalette = Palettes.getPaletteByName(paletteName);
    }

    /**
     * Set the active palette and save to preferences
     * Notifies all registered theme change listeners
     */
    public void setPalette(String paletteName) {
        this.currentPalette = Palettes.getPaletteByName(paletteName);
        prefs.edit()
                .putString(KEY_SELECTED_PALETTE, this.currentPalette.name)
                .apply();

        // Notify all listeners of the theme change
        notifyThemeChanged();
    }

    /**
     * Notify all registered listeners that the theme has changed
     */
    private void notifyThemeChanged() {
        android.util.Log.d("ThemeManager", "notifyThemeChanged: " + currentPalette.name + ", listeners count=" + themeChangeListeners.size());
        for (ThemeChangeListener listener : themeChangeListeners) {
            android.util.Log.d("ThemeManager", "Calling onThemeChanged on listener: " + listener.getClass().getSimpleName());
            listener.onThemeChanged(currentPalette.name);
        }
    }

    /**
     * Register a listener to be notified of theme changes
     */
    public void addThemeChangeListener(ThemeChangeListener listener) {
        if (!themeChangeListeners.contains(listener)) {
            themeChangeListeners.add(listener);
            android.util.Log.d("ThemeManager", "Listener registered: " + listener.getClass().getSimpleName() + ", total listeners: " + themeChangeListeners.size());
        }
    }

    /**
     * Unregister a theme change listener
     */
    public void removeThemeChangeListener(ThemeChangeListener listener) {
        themeChangeListeners.remove(listener);
    }

    /**
     * Get the current active palette
     */
    public ColorPalette getCurrentPalette() {
        return currentPalette;
    }

    /**
     * Get a specific color from the current palette
     */
    @ColorInt
    public int getAccentColor() {
        return currentPalette.accentColor;
    }

    @ColorInt
    public int getAccentColorDark() {
        return currentPalette.accentColorDark;
    }

    @ColorInt
    public int getBackgroundDark() {
        return currentPalette.backgroundDark;
    }

    @ColorInt
    public int getContainerBase() {
        return currentPalette.containerBase;
    }

    @ColorInt
    public int getContainerLight() {
        return currentPalette.containerLight;
    }

    @ColorInt
    public int getTextPrimary() {
        return currentPalette.textPrimary;
    }

    @ColorInt
    public int getTextSecondary() {
        return currentPalette.textSecondary;
    }

    @ColorInt
    public int getAccentSecondary() {
        return currentPalette.accentSecondary;
    }

    @ColorInt
    public int getMeterLow() {
        return currentPalette.meterLow;
    }

    @ColorInt
    public int getMeterMid() {
        return currentPalette.meterMid;
    }

    @ColorInt
    public int getMeterHigh() {
        return currentPalette.meterHigh;
    }

    @ColorInt
    public int getGradientLight() {
        return currentPalette.gradientLight;
    }

    @ColorInt
    public int getGradientDark() {
        return currentPalette.gradientDark;
    }

    @ColorInt
    public int getCircleGradientLight() {
        return currentPalette.circleGradientLight;
    }

    @ColorInt
    public int getCircleGradientDark() {
        return currentPalette.circleGradientDark;
    }

    @ColorInt
    public int getScreenBackgroundColor() {
        return currentPalette.screenBackgroundColor;
    }

    @ColorInt
    public int getGradientCircleLight() {
        return currentPalette.gradientCircleLight;
    }

    @ColorInt
    public int getGradientCircleDark() {
        return currentPalette.gradientCircleDark;
    }

    /**
     * Get the name of the currently selected palette
     */
    public String getCurrentPaletteName() {
        return currentPalette.name;
    }

    /**
     * Get all available palettes (for shop display, etc.)
     */
    public static ColorPalette[] getAllAvailablePalettes() {
        return Palettes.getAllPalettes();
    }
}
