package com.example.ecowattchtechdemo;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;

import com.example.ecowattchtechdemo.theme.ThemeManager;
import com.example.ecowattchtechdemo.theme.ThemeChangeListener;
import com.example.ecowattchtechdemo.theme.ThemeApplier;

public class RecordsActivity extends AppCompatActivity implements ThemeChangeListener {
    private static final String TAG = "RecordsActivity";
    Button backButton;
    private ThemeManager themeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        // Initialize theme manager
        themeManager = ThemeManager.getInstance(this);

        backButton = findViewById((R.id.back_button));

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

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
