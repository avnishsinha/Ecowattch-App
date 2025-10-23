package com.example.ecowattchtechdemo.theme;

import android.graphics.Color;

/**
 * Predefined color palettes for EcoWattch.
 * Contains three main themes: Peach, Lime, and Frost.
 */
public class Palettes {

    /**
     * PEACH Palette - Warm red/coral accent on dark background
     * Accent: Coral red
     * Bottom gradient: Light pink to coral pink
     * Circle gradient: Light pink to coral red
     */
    public static final ColorPalette PEACH = new ColorPalette(
            "PEACH",
            0xFFDB1919,      // accentColor (red)
            0xFFCD232E,      // accentColorDark (darker red)
            0xFF0F0F0F,      // backgroundDark
            0xFF262626,      // containerBase
            0xFF3B3B3B,      // containerLight
            0xFFFFFFFF,      // textPrimary (white)
            0xFFAAAAAA,      // textSecondary (gray)
            0xFFE69696,      // accentSecondary (light red)
            0xFFB4E7B4,      // meterLow (pastel green)
            0xFFFFE082,      // meterMid (pastel yellow)
            0xFFFFA8A8,      // meterHigh (pastel red)
            0xFFFFF5F5,      // gradientLight (very light pink)
            0xFFE69696,      // gradientDark (coral pink)
            0xFFFFF5F5,      // circleGradientLight (very light pink)
            0xFFE69696,      // circleGradientDark (coral pink)
            0xFF0F0F0F,      // screenBackgroundColor (dark, stays same)
            0xFFFFF5F5,      // gradientCircleLight (very light pink)
            0xFFE69696       // gradientCircleDark (coral pink)
    );

    /**
     * LIME Palette - Bright green accent on dark background
     * Accent: Vibrant lime green
     * Bottom gradient: Light mint to lime green
     * Circle gradient: Light mint to lime green
     */
    public static final ColorPalette LIME = new ColorPalette(
            "LIME",
            0xFF27B637,      // accentColor (lime green)
            0xFF1E8A2A,      // accentColorDark (darker lime)
            0xFF0F0F0F,      // backgroundDark
            0xFF1A3A1F,      // containerBase (dark greenish)
            0xFF2A5A2F,      // containerLight (medium greenish)
            0xFFFFFFFF,      // textPrimary (white)
            0xFFAAAAAA,      // textSecondary (gray)
            0xFF66BB6A,      // accentSecondary (light green)
            0xFF81C784,      // meterLow (pastel green)
            0xFFFFE082,      // meterMid (pastel yellow)
            0xFF66BB6A,      // meterHigh (pastel green)
            0xFFC8E6C9,      // gradientLight (very light green)
            0xFF4CAF50,      // gradientDark (green)
            0xFFC8E6C9,      // circleGradientLight (very light green)
            0xFF4CAF50,      // circleGradientDark (lime green)
            0xFF0F0F0F,      // screenBackgroundColor (black - same as PEACH)
            0xFFC8E6C9,      // gradientCircleLight (very light green)
            0xFF4CAF50       // gradientCircleDark (green)
    );

    /**
     * FROST Palette - Cyan/turquoise accent on dark background
     * Accent: Bright cyan
     * Bottom gradient: Light cyan to teal
     * Circle gradient: Light cyan to cyan
     */
    public static final ColorPalette FROST = new ColorPalette(
            "FROST",
            0xFF00BCD4,      // accentColor (cyan)
            0xFF0097A7,      // accentColorDark (darker cyan)
            0xFF1F2F3F,      // backgroundDark (dark bluish-gray)
            0xFF1A3A4A,      // containerBase (dark teal)
            0xFF2A5A7A,      // containerLight (medium teal)
            0xFFFFFFFF,      // textPrimary (white)
            0xFFB0BEC5,      // textSecondary (light gray-blue)
            0xFF80DEEA,      // accentSecondary (light cyan)
            0xFFB2DFDB,      // meterLow (pastel teal)
            0xFFFFE082,      // meterMid (pastel yellow)
            0xFF80DEEA,      // meterHigh (pastel cyan)
            0xFFB2EBF2,      // gradientLight (very light cyan)
            0xFF4DD0E1,      // gradientDark (cyan)
            0xFFB2EBF2,      // circleGradientLight (very light cyan)
            0xFF4DD0E1,      // circleGradientDark (cyan)
            0xFF0F0F0F,      // screenBackgroundColor (black - same as PEACH)
            0xFFB2EBF2,      // gradientCircleLight (very light cyan)
            0xFF4DD0E1       // gradientCircleDark (cyan)
    );

    /**
     * Get palette by name
     */
    public static ColorPalette getPaletteByName(String name) {
        if (name == null) return PEACH; // Default to PEACH

        switch (name.toUpperCase()) {
            case "LIME":
                return LIME;
            case "FROST":
                return FROST;
            case "PEACH":
            default:
                return PEACH;
        }
    }

    /**
     * Get all available palettes (for shop display)
     */
    public static ColorPalette[] getAllPalettes() {
        return new ColorPalette[]{PEACH, LIME, FROST};
    }
}
