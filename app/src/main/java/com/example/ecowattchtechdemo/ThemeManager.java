package com.example.ecowattchtechdemo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.view.Window;

public class ThemeManager {
    private Context context;
    private SharedPreferences prefs;

    public ThemeManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE);
    }

    public int getColor(String key, String defaultHex) {
        return Color.parseColor(prefs.getString(key, defaultHex));
    }

    public void applyToActivity(Activity activity) {
        int backgroundDark = getColor("background_dark", "#1B1B1B");
        int backgroundLight = getColor("background_light", "#262626");
        int primaryColor = getColor("primary_color", "#FFFFFF");
        int secondaryColor = getColor("secondary_color", "#AAAAAA");
        int accentColor = getColor("accent_color", "#CD232E");

        View root = activity.findViewById(android.R.id.content);
        root.setBackgroundColor(backgroundDark);

        Window window = activity.getWindow();
        window.setStatusBarColor(primaryColor);
    }
}
