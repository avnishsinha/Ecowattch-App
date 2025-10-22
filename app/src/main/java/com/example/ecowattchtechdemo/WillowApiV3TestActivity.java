package com.example.ecowattchtechdemo;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ecowattchtechdemo.willow.WillowEnergyDataManager;
import com.example.ecowattchtechdemo.willow.WillowApiV3Config;
import com.example.ecowattchtechdemo.willow.models.EnergyDataResponse;

/**
 * Test activity for Willow API v3 integration
 * Allows testing authentication and energy data retrieval
 */
public class WillowApiV3TestActivity extends AppCompatActivity {
    
    private static final String TAG = "WillowApiV3Test";
    
    // UI Components
    private EditText organizationInput;
    private EditText clientIdInput;
    private EditText clientSecretInput;
    private TextView statusText;
    private Button authButton;
    private Button getTinsleyButton;
    private Button getGabaldonButton;
    private Button getSechristButton;
    private Button getAllBuildingsButton;
    private ScrollView outputScrollView;
    private TextView outputText;
    
    // API Manager
    private WillowEnergyDataManager energyDataManager;
    private boolean isAuthenticated = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_willow_test);
        
        initializeViews();
        setupClickListeners();
        loadDefaultValues();
        
        Log.d(TAG, "Willow API Test Activity initialized");
    }
    
    private void initializeViews() {
        organizationInput = findViewById(R.id.organization_input);
        clientIdInput = findViewById(R.id.client_id_input);
        clientSecretInput = findViewById(R.id.client_secret_input);
        statusText = findViewById(R.id.status_text);
        authButton = findViewById(R.id.auth_button);
        getTinsleyButton = findViewById(R.id.get_tinsley_button);
        getGabaldonButton = findViewById(R.id.get_gabaldon_button);
        getSechristButton = findViewById(R.id.get_sechrist_button);
        getAllBuildingsButton = findViewById(R.id.get_all_buildings_button);
        outputScrollView = findViewById(R.id.output_scroll);
        outputText = findViewById(R.id.output_text);
        
        // Initially disable data buttons
        setDataButtonsEnabled(false);
        
        updateStatusText("Ready to authenticate", false);
    }
    
    private void setupClickListeners() {
        authButton.setOnClickListener(v -> performAuthentication());
        getTinsleyButton.setOnClickListener(v -> getEnergyData(WillowApiV3Config.TWIN_ID_TINSLEY, "Tinsley Hall"));
        getGabaldonButton.setOnClickListener(v -> getEnergyData(WillowApiV3Config.TWIN_ID_GABALDON, "Gabaldon Hall"));
        getSechristButton.setOnClickListener(v -> getEnergyData(WillowApiV3Config.TWIN_ID_SECHRIST, "Sechrist Hall"));
        getAllBuildingsButton.setOnClickListener(v -> getAllBuildingsData());
    }
    
    private void loadDefaultValues() {
        // Set default values from config or env
        organizationInput.setText(BuildConfig.WILLOW_BASE_URL);
        clientIdInput.setText(BuildConfig.WILLOW_CLIENT_ID);
        clientSecretInput.setText(BuildConfig.WILLOW_CLIENT_SECRET);
        
        appendOutput("🚀 Willow API v3 Test Interface Initialized");
        appendOutput("📋 Credentials loaded from configuration");
        appendOutput("🏢 Building Twin IDs:");
        appendOutput("  • Tinsley Hall: " + WillowApiV3Config.TWIN_ID_TINSLEY);
        appendOutput("  • Gabaldon Hall: " + WillowApiV3Config.TWIN_ID_GABALDON);
        appendOutput("  • Sechrist Hall: " + WillowApiV3Config.TWIN_ID_SECHRIST);
        appendOutput("\n✨ Ready to test - Click 'Authenticate' to begin");
    }
    
    private void performAuthentication() {
        String orgUrl = organizationInput.getText().toString().trim();
        String clientId = clientIdInput.getText().toString().trim();
        String clientSecret = clientSecretInput.getText().toString().trim();
        
        if (orgUrl.isEmpty() || clientId.isEmpty() || clientSecret.isEmpty()) {
            updateStatusText("Please fill in all authentication fields", true);
            return;
        }
        
        updateStatusText("Authenticating...", false);
        authButton.setEnabled(false);
        
        appendOutput("\n🔐 Starting authentication process...");
        appendOutput("📍 Organization URL: " + orgUrl);
        appendOutput("🆔 Client ID: " + clientId.substring(0, 8) + "...");
        
        // Initialize energy data manager with custom URL
        energyDataManager = new WillowEnergyDataManager(orgUrl);
        
        energyDataManager.authenticate(clientId, clientSecret, new WillowEnergyDataManager.AuthenticationCallback() {
            @Override
            public void onSuccess(String token) {
                runOnUiThread(() -> {
                    isAuthenticated = true;
                    updateStatusText("✅ Authentication successful!", false);
                    authButton.setEnabled(true);
                    setDataButtonsEnabled(true);
                    
                    appendOutput("✅ Authentication successful!");
                    appendOutput("🔑 Access token received");
                    appendOutput("🎯 Ready to fetch energy data");
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    isAuthenticated = false;
                    updateStatusText("❌ Authentication failed: " + error, true);
                    authButton.setEnabled(true);
                    setDataButtonsEnabled(false);
                    
                    appendOutput("❌ Authentication failed!");
                    appendOutput("🔍 Error: " + error);
                    appendOutput("💡 Please check your credentials and try again");
                });
            }
        });
    }
    
    private void getEnergyData(String twinId, String buildingName) {
        if (!isAuthenticated) {
            updateStatusText("Please authenticate first", true);
            return;
        }
        
        updateStatusText("Fetching data for " + buildingName + "...", false);
        
        appendOutput("\n🏢 Fetching energy data for " + buildingName);
        appendOutput("🔍 Twin ID: " + twinId);
        
        energyDataManager.getEnergyData(twinId, new WillowEnergyDataManager.EnergyDataCallback() {
            @Override
            public void onSuccess(EnergyDataResponse data) {
                runOnUiThread(() -> {
                    updateStatusText("✅ Data retrieved for " + buildingName, false);
                    displayEnergyData(data);
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    updateStatusText("❌ Failed to get data: " + error, true);
                    appendOutput("❌ Error fetching data for " + buildingName);
                    appendOutput("🔍 Error: " + error);
                });
            }
        });
    }
    
    private void getAllBuildingsData() {
        if (!isAuthenticated) {
            updateStatusText("Please authenticate first", true);
            return;
        }
        
        updateStatusText("Fetching all buildings data...", false);
        appendOutput("\n🏢 Fetching energy data for all buildings...");
        
        // Get data for all buildings sequentially
        getEnergyData(WillowApiV3Config.TWIN_ID_TINSLEY, "Tinsley Hall");
        
        // Add slight delays to avoid overwhelming the API
        new android.os.Handler().postDelayed(() -> 
            getEnergyData(WillowApiV3Config.TWIN_ID_GABALDON, "Gabaldon Hall"), 1000);
        
        new android.os.Handler().postDelayed(() -> 
            getEnergyData(WillowApiV3Config.TWIN_ID_SECHRIST, "Sechrist Hall"), 2000);
    }
    
    private void displayEnergyData(EnergyDataResponse data) {
        appendOutput("\n📊 Energy Data for " + data.getBuildingName() + ":");
        appendOutput("⚡ Current Usage: " + 
            (data.getCurrentUsageKW() != null ? 
                String.format("%.1f kW", data.getCurrentUsageKW()) : "N/A"));
        appendOutput("📈 Daily Total: " + 
            (data.getDailyTotalKWh() != null ? 
                String.format("%.1f kWh", data.getDailyTotalKWh()) : "N/A"));
        appendOutput("🌟 Potential Energy: " + 
            (data.getPotentialEnergy() != null ? 
                String.format("%.1f", data.getPotentialEnergy()) : "N/A"));
        appendOutput("🏆 Leaderboard Position: " + 
            energyDataManager.getBuildingPosition(data.getBuildingName()));
        appendOutput("📅 Last Updated: " + 
            (data.getLastUpdated() != null ? data.getLastUpdated() : "N/A"));
        appendOutput("📡 Data Status: " + data.getStatus());
        appendOutput("✅ Data Available: " + (data.isDataAvailable() ? "Yes" : "No (Using Fallback)"));
        
        Log.d(TAG, "Displayed energy data for " + data.getBuildingName() + ": " + 
            data.getCurrentUsageKW() + " kW");
    }
    
    private void updateStatusText(String status, boolean isError) {
        statusText.setText(status);
        statusText.setTextColor(isError ? 
            getResources().getColor(android.R.color.holo_red_dark) :
            getResources().getColor(android.R.color.holo_green_dark));
    }
    
    private void setDataButtonsEnabled(boolean enabled) {
        getTinsleyButton.setEnabled(enabled);
        getGabaldonButton.setEnabled(enabled);
        getSechristButton.setEnabled(enabled);
        getAllBuildingsButton.setEnabled(enabled);
    }
    
    private void appendOutput(String text) {
        String timestamp = java.text.DateFormat.getTimeInstance().format(new java.util.Date());
        String logEntry = "[" + timestamp + "] " + text + "\n";
        
        outputText.append(logEntry);
        
        // Auto-scroll to bottom
        outputScrollView.post(() -> outputScrollView.fullScroll(View.FOCUS_DOWN));
        
        Log.d(TAG, text);
    }
}
