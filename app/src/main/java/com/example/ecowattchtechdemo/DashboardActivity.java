package com.example.ecowattchtechdemo;

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

// Willow API imports
import com.example.ecowattchtechdemo.willow.WillowEnergyDataManager;
import com.example.ecowattchtechdemo.willow.WillowApiV3Config;
import com.example.ecowattchtechdemo.willow.models.EnergyDataResponse;

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
    
    // Willow API integration
    private WillowEnergyDataManager energyDataManager;
    private boolean isWillowAuthenticated = false;
    private boolean useRealData = false; // Toggle between real and simulated data

    // Meter components
    View meterFill;
    ImageView thresholdIndicator;

    // Meter configuration
    private static final int MAX_USAGE = 600; // 600kw max
    private static final int MIN_USAGE = 0;   // 0kw min
    private int currentUsage = 0;  // Initial value, updated by live data
    private int thresholdValue = 300; // Threshold at 300kw

    // theme manager
    private ThemeManager tm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initializeComponents();
        setupNavigationButtons();
        setupFragment(savedInstanceState);
        startLiveDataUpdates();

        // initialize themeManager
        tm = new ThemeManager(this);
    }

    protected void onStart() {
        super.onStart();
        tm.applyTheme(this);
    }
    
    private void initializeComponents() {
        updateHandler = new Handler(Looper.getMainLooper());
        random = new Random();
        decimalFormat = new DecimalFormat("#,##0");
        
        // Validate BuildConfig environment variables
        Log.d(TAG, "🔐 Environment Variables Status:");
        Log.d(TAG, "  - Willow Base URL: " + (BuildConfig.WILLOW_BASE_URL.isEmpty() ? "❌ Missing" : "✅ Loaded"));
        Log.d(TAG, "  - Client ID: " + (BuildConfig.WILLOW_CLIENT_ID.isEmpty() ? "❌ Missing" : "✅ Loaded (" + BuildConfig.WILLOW_CLIENT_ID.substring(0, 8) + "...)"));
        Log.d(TAG, "  - Client Secret: " + (BuildConfig.WILLOW_CLIENT_SECRET.isEmpty() ? "❌ Missing" : "✅ Loaded"));
        Log.d(TAG, "  - Twin IDs: " + 
              (BuildConfig.TWIN_ID_TINSLEY.isEmpty() ? "❌" : "✅") + " Tinsley, " +
              (BuildConfig.TWIN_ID_GABALDON.isEmpty() ? "❌" : "✅") + " Gabaldon, " +
              (BuildConfig.TWIN_ID_SECHRIST.isEmpty() ? "❌" : "✅") + " Sechrist");
        
        // Initialize Willow API manager
        initializeWillowApi();
        
        Log.d(TAG, "Live data system initialized");
    }
    
    /**
     * Initialize Willow API integration
     */
    private void initializeWillowApi() {
        try {
            energyDataManager = new WillowEnergyDataManager();
            
            // Try to authenticate with stored credentials
            authenticateWithWillow();
            
        } catch (Exception e) {
            Log.w(TAG, "Failed to initialize Willow API, falling back to simulated data", e);
            useRealData = false;
        }
    }
    
    /**
     * Authenticate with Willow API
     */
    private void authenticateWithWillow() {
        // Use credentials from BuildConfig (loaded from local.properties)
        String clientId = BuildConfig.WILLOW_CLIENT_ID;
        String clientSecret = BuildConfig.WILLOW_CLIENT_SECRET;
        
        // Validate that credentials are available
        if (clientId == null || clientId.isEmpty() || clientSecret == null || clientSecret.isEmpty()) {
            Log.e(TAG, "❌ Willow API credentials not found in BuildConfig. Check local.properties file.");
            isWillowAuthenticated = false;
            useRealData = false;
            
            runOnUiThread(() -> {
                if (dashContentFragment != null) {
                    dashContentFragment.updateYesterdaysTotal("API Credentials Missing ❌");
                }
            });
            return;
        }
        
        Log.d(TAG, "🔐 Authenticating with Client ID: " + clientId.substring(0, 8) + "...");
        
        energyDataManager.authenticate(clientId, clientSecret, new WillowEnergyDataManager.AuthenticationCallback() {
            @Override
            public void onSuccess(String token) {
                Log.d(TAG, "✅ Willow API authentication successful!");
                isWillowAuthenticated = true;
                useRealData = true;
                
                runOnUiThread(() -> {
                    // Update UI to indicate real data is being used
                    if (dashContentFragment != null) {
                        dashContentFragment.updateYesterdaysTotal("Connected to Willow API ✅");
                    }
                });
            }
            
            @Override
            public void onError(String error) {
                Log.w(TAG, "❌ Willow API authentication failed: " + error);
                isWillowAuthenticated = false;
                useRealData = false;
                
                runOnUiThread(() -> {
                    // Update UI to indicate simulated data
                    if (dashContentFragment != null) {
                        dashContentFragment.updateYesterdaysTotal("Using Simulated Data 🔄");
                    }
                });
            }
        });
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
        
        // Add long press listener to access Willow API test interface
        logoutButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, WillowApiV3TestActivity.class);
                startActivity(intent);
                return true;
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
     * Update UI with fresh live data (real or simulated)
     */
    private void updateUIWithLiveData() {
        if (dashContentFragment == null || !dashContentFragment.isAdded()) {
            Log.w(TAG, "Fragment not ready for live data update - retrying in 1 second");
            // Retry after a short delay
            updateHandler.postDelayed(() -> updateUIWithLiveData(), 1000);
            return;
        }

        // Rotate through dorms every few updates
        rotateDorm();

        if (useRealData && isWillowAuthenticated) {
            // Fetch real data from Willow API
            fetchRealEnergyData();
        } else {
            // Use simulated data
            updateWithSimulatedData();
        }
    }
    
    /**
     * Fetch real energy data from Willow API
     */
    private void fetchRealEnergyData() {
        String twinId = getCurrentBuildingTwinId();
        
        Log.d(TAG, "🌐 Fetching REAL energy data for " + currentDormName + " (Twin ID: " + twinId + ")");
        
        energyDataManager.getEnergyData(twinId, new WillowEnergyDataManager.EnergyDataCallback() {
            @Override
            public void onSuccess(EnergyDataResponse data) {
                runOnUiThread(() -> {
                    updateUIWithRealData(data);
                });
            }
            
            @Override
            public void onError(String error) {
                Log.w(TAG, "Failed to fetch real data, falling back to simulated: " + error);
                runOnUiThread(() -> {
                    // Fallback to simulated data
                    updateWithSimulatedData();
                });
            }
        });
    }
    
    /**
     * Update UI with real energy data from Willow API
     */
    private void updateUIWithRealData(EnergyDataResponse data) {
        int liveUsage = data.getCurrentUsageAsInt();
        
        // Update the instance variable for meter updates
        this.currentUsage = liveUsage;
        
        Log.d(TAG, "🌐 REAL DATA UPDATE: " + liveUsage + "kW for " + data.getBuildingName() + 
              " (Status: " + data.getStatus() + ")");
        
        // Update current usage (main display)
        dashContentFragment.updateCurrentUsage(liveUsage + "kW");
        
        // Update the energy meter with real usage
        updateMeter(liveUsage, thresholdValue);
        
        // Update dorm status with position
        String position = energyDataManager.getBuildingPosition(data.getBuildingName());
        String statusText = data.getBuildingName() + " - " + (position != null ? position : "LIVE DATA");
        dashContentFragment.updateDormStatus(statusText);
        
        // Use real potential energy if available
        if (data.getPotentialEnergy() != null) {
            dashContentFragment.updatePotentialEnergy(data.getPotentialEnergy().intValue() + " Potential Energy");
        } else {
            // Calculate potential energy based on real usage
            int potentialEnergy = Math.max(0, 300 - (liveUsage - 200));
            dashContentFragment.updatePotentialEnergy(potentialEnergy + " Potential Energy");
        }
        
        // Display real daily total or calculated value
        String dailyTotalText;
        if (data.getDailyTotalKWh() != null) {
            dailyTotalText = "Today's Total: " + decimalFormat.format(data.getDailyTotalAsInt()) + "kWh (Real Data ✅)";
        } else {
            int calculatedTotal = liveUsage * 24;
            dailyTotalText = "Estimated Daily: " + decimalFormat.format(calculatedTotal) + "kWh (Calculated)";
        }
        dashContentFragment.updateYesterdaysTotal(dailyTotalText);
        
        Log.d(TAG, "✅ Real data update completed successfully - Next update in " + (UPDATE_INTERVAL/1000) + " seconds");
    }
    
    /**
     * Update UI with simulated data (fallback)
     */
    private void updateWithSimulatedData() {
        // Generate realistic energy usage data for current dorm
        int baseUsage = getBaseUsageForDorm(currentDormIndex);
        int liveUsage = baseUsage + random.nextInt(50) - 25; // ±25kW variation

        // Update the instance variable for meter updates
        this.currentUsage = liveUsage;

        Log.d(TAG, "🔄 SIMULATED UPDATE: " + liveUsage + "kW for " + currentDormName + " (Position: " + dormPositions[currentDormIndex] + ")");

        // Update current usage (main display)
        dashContentFragment.updateCurrentUsage(liveUsage + "kW");

        // Update the energy meter with new usage
        updateMeter(liveUsage, thresholdValue);

        // Update dorm status with position
        String statusText = currentDormName + " - " + dormPositions[currentDormIndex];
        dashContentFragment.updateDormStatus(statusText);

        // Calculate potential energy based on efficiency (dynamic calculation)
        int optimalUsage = (int) (baseUsage * 0.8); // 80% of base usage is optimal
        int potentialEnergy = Math.max(0, (liveUsage > optimalUsage) ? 
            (int)((double)(liveUsage - optimalUsage) / liveUsage * 1000) : 1000);
        dashContentFragment.updatePotentialEnergy(potentialEnergy + " Potential Energy");

        // Simulate yesterday's total
        int yesterdayTotal = liveUsage * 24 + random.nextInt(1000);
        dashContentFragment.updateYesterdaysTotal("Yesterday's Total: " + decimalFormat.format(yesterdayTotal) + "kWh (Simulated 🔄)");

        Log.d(TAG, "✅ Simulated data update completed successfully - Next update in " + (UPDATE_INTERVAL/1000) + " seconds");
    }
    
    /**
     * Get current building twin ID based on rotation
     */
    private String getCurrentBuildingTwinId() {
        switch (currentDormIndex) {
            case 0: return WillowApiV3Config.TWIN_ID_TINSLEY;
            case 1: return WillowApiV3Config.TWIN_ID_GABALDON;
            case 2: return WillowApiV3Config.TWIN_ID_SECHRIST;
            default: return WillowApiV3Config.TWIN_ID_TINSLEY;
        }
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
     * Get base energy usage for different dorms (dynamic calculation based on real factors)
     */
    private int getBaseUsageForDorm(int dormIndex) {
        // Dynamic calculation based on time of day and building characteristics
        // This removes hardcoded values and makes it more realistic
        int timeOfDayMultiplier = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY);
        int baseLoad = 200 + (timeOfDayMultiplier * 5); // Base load varies with time
        
        // Building efficiency factor (based on actual building characteristics)
        double efficiencyFactor = 1.0;
        switch (dormIndex) {
            case 0: efficiencyFactor = 0.85; // Tinsley - newer building, more efficient
            case 1: efficiencyFactor = 1.0;  // Gabaldon - average efficiency
            case 2: efficiencyFactor = 1.15; // Sechrist - older building, less efficient
            default: efficiencyFactor = 1.0;
        }
        
        return (int) (baseLoad * efficiencyFactor);
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
