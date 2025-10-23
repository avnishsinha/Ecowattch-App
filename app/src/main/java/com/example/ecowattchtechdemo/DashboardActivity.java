package com.example.ecowattchtechdemo;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.widget.LinearLayout;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.util.Random;
import java.text.DecimalFormat;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.TextView;

public class DashboardActivity extends AppCompatActivity {
    private static final String TAG = "DashboardActivity";
    
    LinearLayout records, shop;
    private DashContentFragment dashContentFragment;
    private Handler updateHandler;
    private Random random;
    private DecimalFormat decimalFormat;
    
    // Live data configuration
    private String currentDormName = "TINSLEY";
    private static final long UPDATE_INTERVAL = 10000; // 10 seconds
    
    // Dorm data for rotation
    private String[] dormNames = {"TINSLEY", "GABALDON", "SECHRIST"};
    private String[] dormPositions = {"1ST PLACE", "2ND PLACE", "3RD PLACE"};
    private int currentDormIndex = 0;
    ImageView logoutButton;

    // Meter components
    View meterFill;
    ImageView thresholdIndicator;

    // Meter configuration
    private static final int MAX_USAGE = 400; // 400kw max
    private static final int MIN_USAGE = 0;   // 0kw min
    private int currentUsage = 280;  // Initial value, updated by live data
    private int thresholdValue = 250; // Threshold at 250kw

    // theme manager
    ThemeManager tm = new ThemeManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initializeComponents();
        setupNavigationButtons();
        setupFragment(savedInstanceState);
        startLiveDataUpdates();

        tm.applyTheme();
    }
    
    private void initializeComponents() {
        updateHandler = new Handler(Looper.getMainLooper());
        random = new Random();
        decimalFormat = new DecimalFormat("#,##0");
        
        Log.d(TAG, "Live data system initialized");
    }
    
    private void setupNavigationButtons() {
        records = findViewById(R.id.records_button);
        shop = findViewById(R.id.shop_button);
        logoutButton = findViewById(R.id.logout_button);

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
    }
    
    private void setupFragment(Bundle savedInstanceState) {

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
            dashContentFragment = new DashContentFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.dash_content_fragment_container, dashContentFragment)
                    .commitNow(); // Use commitNow for immediate availability
            Log.d(TAG, "New fragment created and committed");
        } else {
            dashContentFragment = (DashContentFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.dash_content_fragment_container);
            Log.d(TAG, "Fragment restored from saved state");
        }

        // Initialize meter components
        meterFill = findViewById(R.id.meter_fill);
        thresholdIndicator = findViewById(R.id.threshold_indicator);

        // Initialize meter with default value
        meterFill.post(() -> {
            updateMeter(currentUsage, thresholdValue);
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
     * Start the live data update cycle
     */
    private void startLiveDataUpdates() {
        Log.d(TAG, "Starting live data updates with " + UPDATE_INTERVAL + "ms interval");
        
        // Show initial data immediately
        updateHandler.post(() -> {
            if (dashContentFragment != null && dashContentFragment.isAdded()) {
                updateUIWithLiveData();
            } else {
                // Retry after a short delay if fragment isn't ready
                updateHandler.postDelayed(() -> updateUIWithLiveData(), 200);
            }
        });
        
        // Schedule recurring updates
        scheduleNextUpdate();
    }
    
    /**
     * Schedule the next data update
     */
    private void scheduleNextUpdate() {
        updateHandler.postDelayed(() -> {
            updateUIWithLiveData();
            scheduleNextUpdate(); // Schedule the next update
        }, UPDATE_INTERVAL);
    }
    
    /**
     * Update UI with fresh simulated live data
     */
    private void updateUIWithLiveData() {
        if (dashContentFragment == null || !dashContentFragment.isAdded()) {
            Log.w(TAG, "Fragment not ready for live data update - retrying in 1 second");
            // Retry after a short delay
            updateHandler.postDelayed(() -> updateUIWithLiveData(), 1000);
            return;
        }

        // Rotate through dorms every few updates (more frequent for testing)
        rotateDorm();

        // Generate realistic energy usage data for current dorm
        int baseUsage = getBaseUsageForDorm(currentDormIndex);
        int liveUsage = baseUsage + random.nextInt(50) - 25; // ±25kW variation

        // Update the instance variable for meter updates
        this.currentUsage = liveUsage;

        Log.d(TAG, "🔄 LIVE UPDATE: " + liveUsage + "kW for " + currentDormName + " (Position: " + dormPositions[currentDormIndex] + ")");

        // Update current usage (main display)
        dashContentFragment.updateCurrentUsage(liveUsage + "kW");

        // Update the energy meter with new usage
        updateMeter(liveUsage, thresholdValue);

        // Update dorm status with position
        String statusText = currentDormName + " - " + dormPositions[currentDormIndex];
        dashContentFragment.updateDormStatus(statusText);

        // Calculate potential energy based on usage efficiency
        int potentialEnergy = Math.max(0, 300 - (liveUsage - 200));
        dashContentFragment.updatePotentialEnergy(potentialEnergy + " Potential Energy");

        // Simulate yesterday's total
        int yesterdayTotal = liveUsage * 24 + random.nextInt(1000);
        dashContentFragment.updateYesterdaysTotal("Yesterday's Total: " + decimalFormat.format(yesterdayTotal) + "kWh");

        Log.d(TAG, "✅ Live data update completed successfully - Next update in " + (UPDATE_INTERVAL/1000) + " seconds");
    }
    
    /**
     * Rotate through different dorms every few updates
     */
    private void rotateDorm() {
        // Change dorm every 2 updates (approximately every 20 seconds) - more frequent for testing
        if (random.nextInt(2) == 0) {
            currentDormIndex = (currentDormIndex + 1) % dormNames.length;
            currentDormName = dormNames[currentDormIndex];
            Log.d(TAG, "🏠 Rotated to dorm: " + currentDormName + " (Index: " + currentDormIndex + ")");
        }
    }
    
    /**
     * Manual refresh - switches to next dorm and updates data immediately
     */
    public void manualRefresh() {
        Log.d(TAG, "Manual refresh requested");
        
        // Switch to next dorm
        currentDormIndex = (currentDormIndex + 1) % dormNames.length;
        currentDormName = dormNames[currentDormIndex];
        
        // Update UI immediately
        updateUIWithLiveData();
        
        Log.d(TAG, "Manual refresh completed - switched to: " + currentDormName);
    }
    
    /**
     * Get base energy usage for different dorms (simulate different efficiency levels)
     */
    private int getBaseUsageForDorm(int dormIndex) {
        switch (dormIndex) {
            case 0: return 280; // Tinsley - most efficient (1st place)
            case 1: return 315; // Gabaldon - medium efficiency (2nd place)
            case 2: return 350; // Sechrist - least efficient (3rd place)
            default: return 300;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up the update handler
        if (updateHandler != null) {
            updateHandler.removeCallbacksAndMessages(null);
        }
        Log.d(TAG, "Live data updates stopped");
    }
}
