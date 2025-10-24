package com.example.ecowattchtechdemo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class ThemeManager {
    private final Context context;
    private final SharedPreferences prefs;

    public ThemeManager(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE);
    }

    private Map<String, Integer> loadThemeColorsFromPrefs() {
        Map<String, Integer> themeColors = new HashMap<>();

        // Safely parse color strings (fall back to defaults)
        // tag everything in activities that needs to change, using these keys
        // strings on the left are the tags, ones on the right
        //     are keys for the SharedPreferences
        themeColors.put("primary_color", getColorFromPrefs("primary_color", "#FFFFFF"));
        themeColors.put("secondary_color", getColorFromPrefs("secondary_color", "#AAAAAA"));
        themeColors.put("accent_color", getColorFromPrefs("accent_color", "#CD232E"));

        themeColors.put("primary_text", getColorFromPrefs("primary_color", "#FFFFFF"));
        themeColors.put("secondary_text", getColorFromPrefs("secondary_color", "#AAAAAA"));
        themeColors.put("accent_text", getColorFromPrefs("accent_color", "#CD232E"));

        themeColors.put("background_dark", getColorFromPrefs("background_dark", "#1B1B1B"));
        themeColors.put("background_light", getColorFromPrefs("background_light", "#262626"));

        return themeColors;
    }

    private int getColorFromPrefs(String key, String defaultHex) {
        try {
            return Color.parseColor(prefs.getString(key, defaultHex));
        } catch (Exception e) {
            return Color.parseColor(defaultHex);
        }
    }

    /**
     * applyTheme() - call in onCreate(), after setContentView().
     * or just anytime the theme is changed.
     */
    public void applyTheme(Context context) {
        View root = ((Activity) context).findViewById(android.R.id.content);
        Map<String, Integer> themeColors = loadThemeColorsFromPrefs();
        applyThemeRecursively(root, themeColors);
    }

    private void applyThemeRecursively(View view, Map<String, Integer> colors) {
        if (view == null) return;

        Object tag = view.getTag();

        // Example: text color updates
        if (view instanceof TextView) {
            if ("primary_text".equals(tag)) {
                ((TextView) view).setTextColor(colors.get("primary_text"));
            } else if ("secondary_text".equals(tag)) {
                ((TextView) view).setTextColor(colors.get("secondary_text"));
            } else if ("accent_text".equals(tag)) {
                ((TextView) view).setTextColor(colors.get("accent_text"));
            }
        }

        // Backgrounds
        Drawable bg = view.getBackground();
        if (bg != null) {
            bg = bg.mutate();

            if ("circle_gradient".equals(tag)) {
                if (bg instanceof GradientDrawable) {
                    ((GradientDrawable) bg).setColors(new int[]{
                            colors.get("primary_color"),
                            colors.get("accent_color")
                    });
                } else if (bg instanceof LayerDrawable) {
                    LayerDrawable layers = (LayerDrawable) bg;
                    for (int i = 0; i < layers.getNumberOfLayers(); i++) {
                        Drawable layer = layers.getDrawable(i);
                        if (layer instanceof GradientDrawable) {
                            ((GradientDrawable) layer).setColors(new int[]{
                                    colors.get("primary_color"),
                                    colors.get("accent_color")
                            });
                        }
                    }
                }
            }
        }

        // Recurse into child views
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                applyThemeRecursively(group.getChildAt(i), colors);
            }
        }
    }

    // Save theme colors to SharedPreferences
    public void saveThemeColor(String key, String hexColor) {
        prefs.edit().putString(key, hexColor).apply();
    }
}
