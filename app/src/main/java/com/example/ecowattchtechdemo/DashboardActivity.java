package com.example.ecowattchtechdemo;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.content.Intent;
import android.widget.LinearLayout;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.util.Random;
import java.text.DecimalFormat;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initializeComponents();
        setupNavigationButtons();
        setupFragment(savedInstanceState);
        startLiveDataUpdates();
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
        int currentUsage = baseUsage + random.nextInt(50) - 25; // ±25kW variation
        
        Log.d(TAG, "🔄 LIVE UPDATE: " + currentUsage + "kW for " + currentDormName + " (Position: " + dormPositions[currentDormIndex] + ")");
        
        // Update current usage (main display)
        dashContentFragment.updateCurrentUsage(currentUsage + "kW");
        
        // Update dorm status with position
        String statusText = currentDormName + " - " + dormPositions[currentDormIndex];
        dashContentFragment.updateDormStatus(statusText);
        
        // Calculate potential energy based on usage efficiency
        int potentialEnergy = Math.max(0, 300 - (currentUsage - 200));
        dashContentFragment.updatePotentialEnergy(potentialEnergy + " Potential Energy");
        
        // Simulate yesterday's total
        int yesterdayTotal = currentUsage * 24 + random.nextInt(1000);
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
