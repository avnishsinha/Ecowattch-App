package com.example.ecowattchtechdemo;

import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.TextView;

public class DashboardActivity extends AppCompatActivity {
    LinearLayout records, shop;
    ImageView logoutButton;

    // Meter components
    View meterFill;
    ImageView thresholdIndicator;

    // TEMPORARY: Testing controls (remove when backend is connected)
    Button increaseButton, decreaseButton;

    // Dashboard content display
    TextView currentUsageText, usageUnitText;

    // Meter configuration
    private static final int MAX_USAGE = 400; // 400kw max
    private static final int MIN_USAGE = 0;   // 0kw min
    private static final int INCREMENT_STEP = 10; // Change by 10kw per click
    private int currentUsage = 280;  // Current hardcoded value (280kw from prototype)
    private int thresholdValue = 250; // Threshold at 250kw (example)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        records = findViewById(R.id.records_button);
        shop = findViewById(R.id.shop_button);
        logoutButton = findViewById(R.id.logout_button);

        // TEMPORARY: Initialize testing controls (remove when backend is connected)
        increaseButton = findViewById(R.id.increase_button);
        decreaseButton = findViewById(R.id.decrease_button);

        records.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, RecordsActivity.class);
                startActivity(intent);
            }
        });

        shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, ShopActivity.class);
                startActivity(intent);
            }
        });

        // Logout button click listener - returns to login screen
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate back to LoginSignupActivity and clear the activity stack
                Intent intent = new Intent(DashboardActivity.this, LoginSignupActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        // Only add the fragment if it's not already added
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.dash_content_fragment_container, new DashContentFragment())
                    .commit();
        }

        // Initialize meter components
        meterFill = findViewById(R.id.meter_fill);
        thresholdIndicator = findViewById(R.id.threshold_indicator);

        // Initialize dashboard content display references
        // Note: These are in the fragment, so we need to wait for the view to be created
        meterFill.post(() -> {
            currentUsageText = findViewById(R.id.current_usage_text);
            usageUnitText = findViewById(R.id.usage_unit_text);

            // Update meter with current usage
            updateMeter(currentUsage, thresholdValue);
        });

        // TEMPORARY: Testing controls - Increase button
        increaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentUsage = Math.min(currentUsage + INCREMENT_STEP, MAX_USAGE);
                updateMeter(currentUsage, thresholdValue);
                updateDashboardDisplay();
            }
        });

        // TEMPORARY: Testing controls - Decrease button
        decreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentUsage = Math.max(currentUsage - INCREMENT_STEP, MIN_USAGE);
                updateMeter(currentUsage, thresholdValue);
                updateDashboardDisplay();
            }
        });
    }

    /**
     * Updates the energy meter display
     * @param usage Current energy usage in kw (0-400)
     * @param threshold Threshold value in kw
     */
    private void updateMeter(int usage, int threshold) {
        // Calculate meter fill height as percentage
        float usagePercentage = ((float) usage / MAX_USAGE);
        float thresholdPercentage = ((float) threshold / MAX_USAGE);

        // Update meter fill height
        meterFill.post(() -> {
            ViewGroup.LayoutParams params = meterFill.getLayoutParams();
            int meterHeight = meterFill.getParent() != null ?
                ((View) meterFill.getParent()).getHeight() : 0;
            params.height = (int) (meterHeight * usagePercentage);
            meterFill.setLayoutParams(params);

            // Update meter color based on usage (green to red gradient)
            // Use drawable background to maintain rounded corners
            int color = getMeterColor(usagePercentage);
            android.graphics.drawable.GradientDrawable drawable =
                (android.graphics.drawable.GradientDrawable)
                getResources().getDrawable(R.drawable.meter_fill_shape, null).mutate();
            drawable.setColor(color);
            meterFill.setBackground(drawable);
        });

        // Update threshold indicator position
        thresholdIndicator.post(() -> {
            ViewGroup.MarginLayoutParams params =
                (ViewGroup.MarginLayoutParams) thresholdIndicator.getLayoutParams();
            int meterHeight = thresholdIndicator.getParent() != null ?
                ((View) thresholdIndicator.getParent()).getHeight() : 0;

            // Position from bottom (inverse of percentage)
            int marginBottom = (int) (meterHeight * thresholdPercentage) -
                (thresholdIndicator.getHeight() / 2);
            params.bottomMargin = marginBottom;
            params.topMargin = 0;
            thresholdIndicator.setLayoutParams(params);
        });
    }

    /**
     * Calculate meter color based on usage percentage
     * Pastel Green (low usage) -> Pastel Yellow (medium) -> Pastel Red (high usage)
     */
    private int getMeterColor(float percentage) {
        if (percentage < 0.5f) {
            // Pastel Green to Pastel Yellow (0-50%)
            float localPercentage = percentage * 2; // 0-1 range
            int red = 120 + (int) (localPercentage * 135);  // 120-255
            int green = 220 + (int) (localPercentage * 35); // 220-255
            int blue = 100 - (int) (localPercentage * 50);   // 100-50
            return Color.rgb(red, green, blue);
        } else {
            // Pastel Yellow to Pastel Red (50-100%)
            float localPercentage = (percentage - 0.5f) * 2; // 0-1 range
            int red = 255;
            int green = 255 - (int) (localPercentage * 105); // 255-150
            int blue = 50 - (int) (localPercentage * 50);    // 50-0
            return Color.rgb(red, green, blue);
        }
    }

    /**
     * TEMPORARY: Updates the dashboard display with current usage value
     * This will be replaced when backend API is connected
     */
    private void updateDashboardDisplay() {
        if (currentUsageText != null) {
            currentUsageText.setText(String.valueOf(currentUsage));
        }
    }

}
