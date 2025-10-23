package com.example.ecowattchtechdemo.theme;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.ImageView;
import android.util.Log;

import com.example.ecowattchtechdemo.R;

/**
 * Utility class for applying theme colors consistently across the app.
 * Provides simple, reusable methods for common theme operations.
 */
public class ThemeApplier {
    private static final String TAG = "ThemeApplier";

    /**
     * Update activity background color to match current theme
     */
    public static void updateActivityBackground(Activity activity, ThemeManager themeManager) {
        if (activity == null || themeManager == null) return;

        try {
            View contentView = activity.findViewById(android.R.id.content);
            if (contentView != null) {
                int bgColor = themeManager.getScreenBackgroundColor();
                contentView.setBackgroundColor(bgColor);
                Log.d(TAG, "Updated activity background: " + Integer.toHexString(bgColor));
            }
        } catch (Exception e) {
            Log.w(TAG, "Error updating activity background: " + e.getMessage());
        }
    }

    /**
     * Update gradient circle view with theme colors as oval gradient
     */
    public static void updateGradientCircle(Activity activity, ThemeManager themeManager) {
        if (activity == null || themeManager == null) return;

        try {
            View gradientView = activity.findViewById(R.id.gradient_circle_background);
            if (gradientView != null) {
                int lightColor = themeManager.getGradientCircleLight();
                int darkColor = themeManager.getGradientCircleDark();

                GradientDrawable gradient = new GradientDrawable(
                        GradientDrawable.Orientation.TL_BR,
                        new int[]{lightColor, darkColor}
                );
                gradient.setShape(GradientDrawable.OVAL);
                gradientView.setBackground(gradient);
                Log.d(TAG, "Updated gradient circle: " + Integer.toHexString(lightColor) +
                      " -> " + Integer.toHexString(darkColor));
            }
        } catch (Exception e) {
            Log.w(TAG, "Error updating gradient circle: " + e.getMessage());
        }
    }

    /**
     * Update an ImageView with theme's accent color
     */
    public static void updateImageViewColor(ImageView imageView, ThemeManager themeManager) {
        if (imageView != null && themeManager != null) {
            imageView.setColorFilter(themeManager.getAccentColor());
        }
    }

    /**
     * Apply all common theme updates: background, gradient circle
     * Use this as a shortcut in onThemeChanged() callbacks
     */
    public static void applyThemeToActivity(Activity activity, ThemeManager themeManager) {
        updateActivityBackground(activity, themeManager);
        updateGradientCircle(activity, themeManager);
    }
}
