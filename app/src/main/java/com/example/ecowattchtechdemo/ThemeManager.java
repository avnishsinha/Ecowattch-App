package com.example.ecowattchtechdemo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
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
    private Context context;
    private SharedPreferences prefs;

    public ThemeManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE);
    }

    /**
     * applyTheme() - call in onCreate(), after setContentView().
     * or just anytime the theme is changed.
     * code to call the function correctly: new ThemeManager(this).applyTheme();
     */
    public void applyTheme() {
        Map<String, Integer> themeColors = loadThemeColorsFromPrefs(context);
        View root = ((Activity) context).findViewById(android.R.id.content);
        applyThemeRecursively(root, themeColors);
    }

    private Map<String, Integer> loadThemeColorsFromPrefs(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE);

        Map<String, Integer> themeColors = new HashMap<>();

        // Safely parse color strings (fall back to defaults)
        // tag everything in activities that needs to change using these keys
        themeColors.put("primary_color", Color.parseColor(prefs.getString("primary_color", "#FFFFFF")));
        themeColors.put("secondary_color", Color.parseColor(prefs.getString("secondary_color", "#AAAAAA")));
        themeColors.put("accent_color", Color.parseColor(prefs.getString("accent_color", "#CD232E")));

        themeColors.put("primary_text", Color.parseColor(prefs.getString("primary_color", "#FFFFFF")));
        themeColors.put("secondary_text", Color.parseColor(prefs.getString("secondary_color", "#AAAAAA")));
        themeColors.put("accent_text", Color.parseColor(prefs.getString("accent_color", "#CD232E")));

        themeColors.put("background_dark", Color.parseColor(prefs.getString("background_dark", "#1B1B1B")));
        themeColors.put("background_light", Color.parseColor(prefs.getString("background_light", "#262626")));

        return themeColors;
    }

    private void applyThemeRecursively(View view, Map<String, Integer> colorMap) {
        Object tag = view.getTag();
        if ((tag != null) && colorMap.containsKey(tag.toString())) {
            int color = colorMap.get(tag.toString());

            // determine type of view so color can be set
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(color);
            } else if (view instanceof Button) {
                ((Button) view).setBackgroundTintList(ColorStateList.valueOf(color));
            } else if (view instanceof ImageView) {
                ((ImageView) view).setColorFilter(color);
            } else {
                view.setBackgroundColor(color);
            }
        }

        // recursion

        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                applyThemeRecursively(group.getChildAt(i), colorMap);
            }
        }

        if (view instanceof LinearLayout) {
            LinearLayout group = (LinearLayout) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                applyThemeRecursively(group.getChildAt(i), colorMap);
            }
        }

        if (view instanceof RelativeLayout) {
            RelativeLayout group = (RelativeLayout) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                applyThemeRecursively(group.getChildAt(i), colorMap);
            }
        }
    }
}
