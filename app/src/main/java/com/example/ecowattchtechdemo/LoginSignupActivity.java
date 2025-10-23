package com.example.ecowattchtechdemo;

import android.os.Bundle;
import android.view.View;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ecowattchtechdemo.theme.ThemeManager;
import com.example.ecowattchtechdemo.theme.ThemeChangeListener;
import com.example.ecowattchtechdemo.theme.ThemeApplier;

public class LoginSignupActivity extends AppCompatActivity implements ThemeChangeListener {
    private static final String TAG = "LoginSignupActivity";
    private ThemeManager themeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_signup);

        // Initialize theme manager
        themeManager = ThemeManager.getInstance(this);

        // Only add the fragment if it's not already added
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.login_signup_fragment_container, new LoginFragment())
                    .commit();
        }

        // Register as theme change listener and apply initial theme
        if (themeManager != null) {
            themeManager.addThemeChangeListener(this);
            onThemeChanged(themeManager.getCurrentPaletteName());
        }
    }

    @Override
    public void onThemeChanged(String newPaletteName) {
        Log.d(TAG, "onThemeChanged called: " + newPaletteName);
        ThemeApplier.applyThemeToActivity(this, themeManager);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (themeManager != null) {
            themeManager.removeThemeChangeListener(this);
        }
    }
}
