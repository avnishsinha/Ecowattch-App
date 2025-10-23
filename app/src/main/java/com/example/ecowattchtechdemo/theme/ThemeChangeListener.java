package com.example.ecowattchtechdemo.theme;

/**
 * Interface for listening to theme changes across the app
 * Activities can implement this to receive notifications when the theme changes
 */
public interface ThemeChangeListener {
    /**
     * Called when the theme palette changes
     * @param newPaletteName The name of the newly selected palette (e.g., "PEACH", "LIME", "FROST")
     */
    void onThemeChanged(String newPaletteName);
}
