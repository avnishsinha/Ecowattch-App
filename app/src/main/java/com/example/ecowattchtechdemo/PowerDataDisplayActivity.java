package com.example.ecowattchtechdemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecowattchtechdemo.api.WillowApiV3Service;
import com.example.ecowattchtechdemo.config.WillowApiV3Config;
import com.example.ecowattchtechdemo.models.WillowTimeSeriesData;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Activity for displaying electrical power consumption data from Tinsley and Gabaldon buildings
 * Shows real-time power usage, calculates kWh, and provides basic energy analysis
 */
public class PowerDataDisplayActivity extends AppCompatActivity {
    private static final String TAG = "PowerDataDisplay";
    
    // Building Twin IDs for electrical power sensors
    // Note: These should be updated with actual power sensor twin IDs from your Willow organization
    private static final String TINSLEY_POWER_TWIN_ID = "PNT9CnuLmTV4tkZwAigypXrnY"; // Tinsley building twin ID
    private static final String GABALDON_POWER_TWIN_ID = "PNT6hrRL8shRqbwLaXsbhDrBC"; // Gabaldon building twin ID
    
    private WillowApiV3Service apiService;
    private Handler mainHandler;
    
    // UI Components
    private Button refreshDataButton;
    private Button toggleBuildingButton;
    private ProgressBar progressBar;
    private TextView buildingNameText;
    private TextView currentPowerText;
    private TextView totalKwhText;
    private TextView averagePowerText;
    private TextView dataPointsCountText;
    private TextView lastUpdateText;
    private RecyclerView powerDataRecyclerView;
    private PowerDataAdapter powerDataAdapter;
    
    // Data
    private List<PowerDataPoint> powerDataPoints;
    private boolean showingTinsley = true;
    private String currentTwinId;
    private DecimalFormat decimalFormat;
    private SimpleDateFormat timeFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power_data_display);
        
        initComponents();
        setupUI();
        loadInitialData();
    }
    
    private void initComponents() {
        apiService = new WillowApiV3Service();
        mainHandler = new Handler(Looper.getMainLooper());
        powerDataPoints = new ArrayList<>();
        powerDataAdapter = new PowerDataAdapter(powerDataPoints);
        currentTwinId = TINSLEY_POWER_TWIN_ID;
        
        // Configure API service with credentials from config
        // TODO: Move to secure configuration or environment variables
        apiService.setOrganization("https://northernarizonauniversity.app.willowinc.com/");
        apiService.setCredentials("YOUR_CLIENT_ID_HERE", "YOUR_CLIENT_SECRET_HERE");
        
        // Formatters
        decimalFormat = new DecimalFormat("#,##0.00");
        timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
    }
    
    private void setupUI() {
        // Initialize views
        refreshDataButton = findViewById(R.id.refresh_data_button);
        toggleBuildingButton = findViewById(R.id.toggle_building_button);
        progressBar = findViewById(R.id.progress_bar);
        buildingNameText = findViewById(R.id.building_name_text);
        currentPowerText = findViewById(R.id.current_power_text);
        totalKwhText = findViewById(R.id.total_kwh_text);
        averagePowerText = findViewById(R.id.average_power_text);
        dataPointsCountText = findViewById(R.id.data_points_count_text);
        lastUpdateText = findViewById(R.id.last_update_text);
        powerDataRecyclerView = findViewById(R.id.power_data_recycler_view);
        
        // Setup RecyclerView
        powerDataRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        powerDataRecyclerView.setAdapter(powerDataAdapter);
        
        // Set click listeners
        refreshDataButton.setOnClickListener(v -> loadPowerData());
        toggleBuildingButton.setOnClickListener(v -> toggleBuilding());
        
        // Initial UI state
        updateBuildingDisplay();
    }
    
    private void loadInitialData() {
        // Check if we need to set up credentials first
        if (!isCredentialsConfigured()) {
            showCredentialSetupDialog();
            return;
        }
        
        Toast.makeText(this, "Loading real power data from Willow API...", Toast.LENGTH_SHORT).show();
        loadPowerData();
    }
    
    private boolean isCredentialsConfigured() {
        // Check if API service is properly configured with credentials
        return apiService != null;
    }
    
    private void showCredentialSetupDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Setup Required")
            .setMessage("This demo requires API credentials to be configured.\n\n" +
                       "Please:\n" +
                       "1. Get credentials from the Willow API test screen\n" +
                       "2. Update PowerDataDisplayActivity.java with your credentials\n" +
                       "3. Or use the 'Willow API v3' button to test authentication first")
            .setPositiveButton("Go to API Test", (dialog, which) -> {
                Intent intent = new Intent(this, WillowApiV3TestActivity.class);
                startActivity(intent);
                finish();
            })
            .setNegativeButton("Load Demo Data", (dialog, which) -> {
                loadDemoData();
            })
            .show();
    }
    
    private void loadDemoData() {
        // Show demo data when credentials aren't available
        setLoading(true);
        
        // Simulate loading delay
        new android.os.Handler(Looper.getMainLooper()).postDelayed(() -> {
            showDemoData();
            setLoading(false);
        }, 1500);
    }
    
    private void showDemoData() {
        // Create sample power data for demonstration
        powerDataPoints.clear();
        
        // Generate 24 hours of sample data
        long now = System.currentTimeMillis();
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        
        double totalKwh = 0;
        double totalPowerKw = 0;
        
        for (int i = 0; i < 24; i++) {
            long timestamp = now - (i * 60 * 60 * 1000); // Every hour
            
            // Simulate realistic building power consumption (50-120 kW)
            double basePower = 75; // Base load
            double variablePower = 45 * Math.sin((i * Math.PI) / 12); // Daily cycle
            double randomVariation = (Math.random() - 0.5) * 20; // Random variation
            double powerKw = basePower + variablePower + randomVariation;
            
            PowerDataPoint point = new PowerDataPoint(
                isoFormat.format(new Date(timestamp)),
                powerKw,
                String.format("%.0f W", powerKw * 1000)
            );
            powerDataPoints.add(0, point); // Add to beginning for chronological order
            
            totalPowerKw += powerKw;
            totalKwh += powerKw; // Approximate kWh for demo
        }
        
        // Update display with demo data
        double currentPowerKw = powerDataPoints.get(powerDataPoints.size() - 1).powerKw;
        double averagePowerKw = totalPowerKw / powerDataPoints.size();
        
        updatePowerDisplay(currentPowerKw, totalKwh, totalPowerKw, powerDataPoints.size());
        powerDataAdapter.notifyDataSetChanged();
        
        // Show demo message
        Toast.makeText(this, "Showing demo data - configure credentials for real data", Toast.LENGTH_LONG).show();
        addLog("📊 Demo Data Loaded");
        addLog("🏢 Building: " + (showingTinsley ? "Tinsley Hall (Demo)" : "Gabaldon Hall (Demo)"));
        addLog("⚡ Power Range: 50-120 kW (simulated)");
        addLog("📈 " + powerDataPoints.size() + " sample data points generated");
        addLog("💡 Configure real credentials for live data");
    }
    
    private void addLog(String message) {
        // Simple logging for demo purposes
        Log.d(TAG, message);
    }
    
    private long parseTimestamp(String timestamp) {
        try {
            // Handle different timestamp formats
            if (timestamp.contains("T")) {
                // ISO format: 2024-01-15T14:30:00Z
                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
                return isoFormat.parse(timestamp).getTime();
            } else {
                // Fallback to current time if format is unexpected
                return System.currentTimeMillis();
            }
        } catch (Exception e) {
            Log.w(TAG, "Failed to parse timestamp: " + timestamp + ", error: " + e.getMessage());
            return System.currentTimeMillis();
        }
    }
    
    private void toggleBuilding() {
        showingTinsley = !showingTinsley;
        currentTwinId = showingTinsley ? TINSLEY_POWER_TWIN_ID : GABALDON_POWER_TWIN_ID;
        updateBuildingDisplay();
        loadPowerData();
    }
    
    private void updateBuildingDisplay() {
        String buildingName = showingTinsley ? "Tinsley Hall" : "Gabaldon Hall";
        buildingNameText.setText(buildingName);
        toggleBuildingButton.setText("Switch to " + (showingTinsley ? "Gabaldon" : "Tinsley"));
    }
    
    private void loadPowerData() {
        setLoading(true);
        
        // Get last 24 hours of power data
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        long now = System.currentTimeMillis();
        long yesterday = now - (24 * 60 * 60 * 1000);
        
        String startTime = isoFormat.format(new Date(yesterday));
        String endTime = isoFormat.format(new Date(now));
        
        Log.d(TAG, "Loading power data for twin: " + currentTwinId);
        
        apiService.getTimeSeries(currentTwinId, startTime, endTime, 1000, new WillowApiV3Service.ApiCallback<List<WillowTimeSeriesData>>() {
            @Override
            public void onSuccess(List<WillowTimeSeriesData> timeSeriesData) {
                mainHandler.post(() -> {
                    processPowerData(timeSeriesData);
                    setLoading(false);
                });
            }
            
            @Override
            public void onError(String error) {
                mainHandler.post(() -> {
                    String errorMessage = "Error loading data: " + error;
                    Toast.makeText(PowerDataDisplayActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Failed to load power data: " + error);
                    
                    // If API fails, offer to show demo data
                    new androidx.appcompat.app.AlertDialog.Builder(PowerDataDisplayActivity.this)
                        .setTitle("API Error")
                        .setMessage("Failed to load real data from Willow API.\n\n" + 
                                   "Error: " + error + "\n\n" +
                                   "Would you like to see demo data instead?")
                        .setPositiveButton("Show Demo Data", (dialog, which) -> {
                            loadDemoData();
                        })
                        .setNegativeButton("Retry", (dialog, which) -> {
                            loadPowerData();
                        })
                        .show();
                    
                    setLoading(false);
                });
            }
        });
    }
    
    private void processPowerData(List<WillowTimeSeriesData> timeSeriesData) {
        powerDataPoints.clear();
        
        if (timeSeriesData == null || timeSeriesData.isEmpty()) {
            showNoDataMessage();
            return;
        }
        
        double totalPowerKw = 0;
        double totalKwh = 0;
        double currentPowerKw = 0;
        int validDataPoints = 0;
        
        // Process each data point
        for (int i = 0; i < timeSeriesData.size(); i++) {
            WillowTimeSeriesData dataPoint = timeSeriesData.get(i);
            
            try {
                // Assuming the data is in watts, convert to kilowatts
                double powerWatts = dataPoint.getScalarValue();
                double powerKw = powerWatts / 1000.0;
                
                // Create power data point for display
                PowerDataPoint powerPoint = new PowerDataPoint(
                    dataPoint.getSourceTimestamp(),
                    powerKw,
                    dataPoint.getFormattedValue()
                );
                powerDataPoints.add(powerPoint);
                
                totalPowerKw += powerKw;
                validDataPoints++;
                
                // Calculate kWh (approximate - assumes constant power between readings)
                if (i > 0) {
                    try {
                        // Parse timestamps to calculate time difference
                        long currentTimestamp = parseTimestamp(dataPoint.getSourceTimestamp());
                        long previousTimestamp = parseTimestamp(timeSeriesData.get(i-1).getSourceTimestamp());
                        
                        long timeDiffMs = currentTimestamp - previousTimestamp;
                        double timeDiffHours = timeDiffMs / (1000.0 * 60.0 * 60.0);
                        totalKwh += powerKw * Math.abs(timeDiffHours); // Use absolute value to handle any order issues
                    } catch (Exception e) {
                        Log.w(TAG, "Failed to calculate time difference: " + e.getMessage());
                        // Fallback: assume 1 hour between readings
                        totalKwh += powerKw;
                    }
                }
                
                // Current power is the latest reading
                if (i == timeSeriesData.size() - 1) {
                    currentPowerKw = powerKw;
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Error processing data point: " + e.getMessage());
            }
        }
        
        // Update UI with calculations
        updatePowerDisplay(currentPowerKw, totalKwh, totalPowerKw, validDataPoints);
        
        // Update RecyclerView
        powerDataAdapter.notifyDataSetChanged();
        
        Log.d(TAG, "Processed " + validDataPoints + " power data points");
    }
    
    private void updatePowerDisplay(double currentPowerKw, double totalKwh, double totalPowerKw, int dataPointCount) {
        // Current power
        currentPowerText.setText(decimalFormat.format(currentPowerKw) + " kW");
        
        // Total energy consumption
        totalKwhText.setText(decimalFormat.format(totalKwh) + " kWh");
        
        // Average power
        double averagePowerKw = dataPointCount > 0 ? totalPowerKw / dataPointCount : 0;
        averagePowerText.setText(decimalFormat.format(averagePowerKw) + " kW");
        
        // Data points count
        dataPointsCountText.setText(dataPointCount + " readings");
        
        // Last update time
        lastUpdateText.setText("Updated: " + timeFormat.format(new Date()));
        
        // Show cost estimate (assuming $0.12 per kWh)
        double estimatedCost = totalKwh * 0.12;
        Toast.makeText(this, 
            String.format("24h Energy Cost: $%.2f", estimatedCost), 
            Toast.LENGTH_LONG).show();
    }
    
    private void showNoDataMessage() {
        currentPowerText.setText("No Data");
        totalKwhText.setText("0.00 kWh");
        averagePowerText.setText("0.00 kW");
        dataPointsCountText.setText("0 readings");
        lastUpdateText.setText("No data available");
        
        Toast.makeText(this, "No power data available for this building", Toast.LENGTH_LONG).show();
    }
    
    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        refreshDataButton.setEnabled(!loading);
        toggleBuildingButton.setEnabled(!loading);
    }
    
    // Data class for power readings
    public static class PowerDataPoint {
        public String timestamp;
        public double powerKw;
        public String rawValue;
        
        public PowerDataPoint(String timestamp, double powerKw, String rawValue) {
            this.timestamp = timestamp;
            this.powerKw = powerKw;
            this.rawValue = rawValue;
        }
    }
}
