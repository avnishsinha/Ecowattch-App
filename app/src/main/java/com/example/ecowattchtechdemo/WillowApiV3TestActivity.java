package com.example.ecowattchtechdemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecowattchtechdemo.api.WillowApiV3Service;
import com.example.ecowattchtechdemo.config.WillowApiV3Config;
import com.example.ecowattchtechdemo.models.OAuth2TokenResponse;
import com.example.ecowattchtechdemo.models.WillowTwinV3;
import com.example.ecowattchtechdemo.models.WillowTimeSeriesData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Activity for testing Willow API v3 with OAuth2 authentication
 * 
 * This activity demonstrates the complete Willow API v3 integration including:
 * - OAuth2 Client Credentials Grant authentication
 * - Model discovery and exploration
 * - Building/twin search using advanced filters
 * - Twin data retrieval with relationships
 * - Time series data access
 * 
 * SETUP INSTRUCTIONS:
 * 1. Get OAuth2 credentials from your Willow administrator:
 *    - Client ID (UUID format)
 *    - Client Secret (secure string)
 *    - Organization URL or name
 * 
 * 2. Pre-filled credentials are for Northern Arizona University demo.
 *    Replace with your organization's credentials for testing.
 * 
 * TESTING WORKFLOW:
 * 1. Set Credentials - Configure OAuth2 authentication
 * 2. Test Authentication - Verify connection and get access token
 * 3. Get Models - Explore available data models in your organization
 * 4. Search Buildings - Find building twins using flexible search
 * 5. Test Time Series - Access sensor data (requires valid twin IDs)
 * 
 * API FEATURES DEMONSTRATED:
 * - Organization-specific endpoints
 * - Paginated responses
 * - Advanced filtering with multiple criteria
 * - Property-based search across multiple fields
 * - Relationship inclusion in responses
 * - Error handling and troubleshooting guidance
 * 
 * @author EcoWattch Team
 * @version 3.0 - Updated for Willow API v3
 */
public class WillowApiV3TestActivity extends AppCompatActivity {
    private static final String TAG = "WillowApiV3Test";
    
    private WillowApiV3Service apiService;
    private Handler mainHandler;
    
    // UI Components
    private EditText organizationInput;
    private EditText clientIdInput;
    private EditText clientSecretInput;
    private Button setCredentialsButton;
    private Button testAuthButton;
    private Button searchBuildingsButton;
    private Button getTimeSeriesButton;
    private Button getModelsButton;
    private ProgressBar progressBar;
    private TextView statusText;
    private RecyclerView logsRecyclerView;
    private WillowTestLogsAdapter logsAdapter;
    
    private List<String> logMessages;
    private boolean isCredentialsSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_willow_api_v3_test);
        
        initComponents();
        setupUI();
    }
    
    private void initComponents() {
        apiService = new WillowApiV3Service();
        mainHandler = new Handler(Looper.getMainLooper());
        logMessages = new ArrayList<>();
        logsAdapter = new WillowTestLogsAdapter(logMessages);
    }
    
    private void setupUI() {
        // Initialize views
        organizationInput = findViewById(R.id.organization_input);
        clientIdInput = findViewById(R.id.client_id_input);
        clientSecretInput = findViewById(R.id.client_secret_input);
        setCredentialsButton = findViewById(R.id.set_credentials_button);
        testAuthButton = findViewById(R.id.test_auth_button);
        searchBuildingsButton = findViewById(R.id.search_buildings_button);
        getTimeSeriesButton = findViewById(R.id.get_time_series_button);
        getModelsButton = findViewById(R.id.get_models_button);
        progressBar = findViewById(R.id.progress_bar);
        statusText = findViewById(R.id.status_text);
        logsRecyclerView = findViewById(R.id.logs_recycler_view);
        
        // Setup RecyclerView
        logsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        logsRecyclerView.setAdapter(logsAdapter);
        
        // Set default values - TODO: Move to secure configuration
        organizationInput.setText("https://northernarizonauniversity.app.willowinc.com/");
        clientIdInput.setText("YOUR_CLIENT_ID_HERE");
        clientSecretInput.setText("YOUR_CLIENT_SECRET_HERE");        // Set click listeners
        setCredentialsButton.setOnClickListener(v -> setCredentials());
        testAuthButton.setOnClickListener(v -> testAuthentication());
        searchBuildingsButton.setOnClickListener(v -> searchBuildings());
        getTimeSeriesButton.setOnClickListener(v -> getTimeSeriesData());
        getModelsButton.setOnClickListener(v -> getModels());
        
        // Initial state
        updateUIState(false);
        addLog("🚀 Welcome to Willow API v3 Tester");
        addLog("📋 Please provide your OAuth2 credentials:");
        addLog("   • Organization URL or name (e.g., 'https://yourorg.app.willowinc.com/')");
        addLog("   • Client ID (provided by Willow)");
        addLog("   • Client Secret (provided by Willow)");
        addLog("");
        addLog("✅ PRE-FILLED: Northern Arizona University credentials");
        addLog("🔧 Ready to test! Follow the numbered buttons in order:");
        addLog("   1. Test Authentication - Verify OAuth2 connection");
        addLog("   2. Get Models - List available data models");
        addLog("   3. Search Buildings - Find building twins");
        addLog("   4. Test Time Series - Access sensor data");
        addLog("");
        addLog("⚠️ IMPORTANT: Never commit real credentials to version control!");
    }
    
    private void setCredentials() {
        String organization = organizationInput.getText().toString().trim();
        String clientId = clientIdInput.getText().toString().trim();
        String clientSecret = clientSecretInput.getText().toString().trim();
        
        if (organization.isEmpty() || clientId.isEmpty() || clientSecret.isEmpty()) {
            Toast.makeText(this, "Please enter all credentials", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (organization.equals("your-organization-name") || 
            organization.equals("https://your-organization.app.willowinc.com/") ||
            clientId.equals("your-client-id") || 
            clientSecret.equals("your-client-secret")) {
            Toast.makeText(this, "Please enter real credentials", Toast.LENGTH_LONG).show();
            addLog("❌ Please replace placeholder values with real credentials");
            return;
        }
        
        // Validate organization format (can be full URL or just organization name)
        if (!isValidOrganizationInput(organization)) {
            Toast.makeText(this, "Invalid organization format", Toast.LENGTH_SHORT).show();
            addLog("❌ Organization should be either a full URL (https://org.app.willowinc.com/) or organization name");
            return;
        }
        
        // Set credentials in API service
        apiService.setOrganization(organization);
        apiService.setCredentials(clientId, clientSecret);
        
        isCredentialsSet = true;
        updateUIState(false);
        
        addLog("✅ Credentials configured successfully");
        addLog("📍 Organization: " + organization);
        addLog("🔑 Client ID: " + clientId.substring(0, Math.min(8, clientId.length())) + "...");
        addLog("🔐 Client Secret: " + clientSecret.substring(0, Math.min(8, clientSecret.length())) + "...");
        addLog("🌐 API Base URL: " + WillowApiV3Config.formatBaseUrl(organization));
        addLog("");
        addLog("▶️ Ready to test! Start with '1️⃣ Test Authentication'.");
        addLog("   Then proceed through the numbered buttons in order.");
        
        Toast.makeText(this, "Credentials set! Ready to test API", Toast.LENGTH_SHORT).show();
    }
    
    private void testAuthentication() {
        if (!isCredentialsSet) {
            Toast.makeText(this, "Please set credentials first", Toast.LENGTH_SHORT).show();
            return;
        }
        
        updateUIState(true);
        addLog("🔐 Testing OAuth2 authentication...");
        addLog("   Organization: " + organizationInput.getText().toString());
        addLog("   Endpoint: " + WillowApiV3Config.formatBaseUrl(organizationInput.getText().toString()));
        
        apiService.authenticate(new WillowApiV3Service.ApiCallback<OAuth2TokenResponse>() {
            @Override
            public void onSuccess(OAuth2TokenResponse token) {
                mainHandler.post(() -> {
                    addLog("✅ Authentication successful!");
                    addLog("   Token Type: " + token.getTokenType());
                    addLog("   Expires In: " + token.getExpiresIn() + " seconds");
                    addLog("   Access Token: " + token.getAccessToken().substring(0, Math.min(20, token.getAccessToken().length())) + "...");
                    addLog("");
                    addLog("🎉 OAuth2 connection established!");
                    addLog("▶️ Next: Try '2️⃣ Get Models' to explore available data models.");
                    
                    updateUIState(false);
                    Toast.makeText(WillowApiV3TestActivity.this, "Authentication successful!", Toast.LENGTH_SHORT).show();
                });
            }
            
            @Override
            public void onError(String error) {
                mainHandler.post(() -> {
                    addLog("❌ Authentication failed: " + error);
                    addLog("");
                    addLog("💡 Troubleshooting tips:");
                    addLog("   • Verify client ID and secret are correct");
                    addLog("   • Check organization name matches exactly");
                    addLog("   • Ensure credentials have proper permissions");
                    addLog("   • Check network connectivity");
                    addLog("   • Review logcat for detailed error info");
                    
                    updateUIState(false);
                    Toast.makeText(WillowApiV3TestActivity.this, "Auth failed: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }
    
    private void searchBuildings() {
        if (!isCredentialsSet) {
            Toast.makeText(this, "Please set credentials first", Toast.LENGTH_SHORT).show();
            return;
        }
        
        updateUIState(true);
        addLog("🏢 Searching for buildings...");
        addLog("   Query: Buildings containing 'Tinsley'");
        addLog("   Using API endpoint: " + apiService.getBaseUrl() + "/twins");
        
        apiService.searchBuildingByName("Tinsley", new WillowApiV3Service.ApiCallback<List<WillowTwinV3>>() {
            @Override
            public void onSuccess(List<WillowTwinV3> buildings) {
                mainHandler.post(() -> {
                    addLog("✅ Building search completed!");
                    addLog("   Found " + buildings.size() + " building(s)");
                    addLog("");
                    
                    if (buildings.isEmpty()) {
                        addLog("⚠️ No buildings found matching 'Tinsley'");
                        addLog("💡 Trying broader search for all buildings...");
                        
                        // Try a broader search
                        apiService.getAllBuildings(new WillowApiV3Service.ApiCallback<List<WillowTwinV3>>() {
                            @Override
                            public void onSuccess(List<WillowTwinV3> allBuildings) {
                                mainHandler.post(() -> {
                                    addLog("✅ Broader search completed!");
                                    addLog("   Found " + allBuildings.size() + " building-related twin(s)");
                                    addLog("");
                                    
                                    if (allBuildings.isEmpty()) {
                                        addLog("⚠️ No building-related twins found in this organization");
                                        addLog("💡 Possible reasons:");
                                        addLog("   • No buildings configured in this organization");
                                        addLog("   • Buildings might use different model types");
                                        addLog("   • Different data structure than expected");
                                    } else {
                                        addLog("🏢 Available building-related twins:");
                                        for (int i = 0; i < Math.min(10, allBuildings.size()); i++) {
                                            WillowTwinV3 building = allBuildings.get(i);
                                            addLog("   " + (i + 1) + ". ID: " + building.getId());
                                            addLog("      Name: " + building.getDisplayName());
                                            addLog("      Model: " + building.getModelId());
                                            addLog("");
                                        }
                                        if (allBuildings.size() > 10) {
                                            addLog("   ... and " + (allBuildings.size() - 10) + " more");
                                        }
                                    }
                                    updateUIState(false);
                                });
                            }
                            
                            @Override
                            public void onError(String error) {
                                mainHandler.post(() -> {
                                    addLog("❌ Broader search also failed: " + error);
                                    updateUIState(false);
                                });
                            }
                        });
                        return; // Don't update UI state yet, wait for broader search
                    } else {
                        for (int i = 0; i < buildings.size(); i++) {
                            WillowTwinV3 building = buildings.get(i);
                            addLog("🏢 Building " + (i + 1) + ":");
                            addLog("   ID: " + building.getId());
                            addLog("   Name: " + building.getDisplayName());
                            addLog("   Model: " + building.getModelId());
                            addLog("   Has Relationships: " + building.hasRelationships());
                            if (building.getContents() != null) {
                                addLog("   Properties: " + building.getContents().size() + " items");
                            }
                            addLog("");
                        }
                        addLog("🎯 Success! Buildings found and parsed correctly.");
                    }
                    
                    updateUIState(false);
                });
            }
            
            @Override
            public void onError(String error) {
                mainHandler.post(() -> {
                    addLog("❌ Building search failed: " + error);
                    addLog("");
                    addLog("💡 Troubleshooting:");
                    if (error.contains("401") || error.contains("token")) {
                        addLog("   • Authentication issue - try 'Test Authentication' first");
                    } else if (error.contains("404")) {
                        addLog("   • API endpoint might not exist");
                        addLog("   • Organization name might be incorrect");
                    } else if (error.contains("parsing") || error.contains("JSON")) {
                        addLog("   • API response format changed");
                        addLog("   • Check logcat for detailed JSON response");
                    } else {
                        addLog("   • Check network connectivity");
                        addLog("   • Verify API permissions");
                        addLog("   • Review logcat for detailed error info");
                    }
                    
                    updateUIState(false);
                });
            }
        });
    }
    
    private void getTimeSeriesData() {
        if (!isCredentialsSet) {
            Toast.makeText(this, "Please set credentials first", Toast.LENGTH_SHORT).show();
            return;
        }
        
        updateUIState(true);
        addLog("📊 Testing time series data access...");
        addLog("   NOTE: This uses a sample twin ID for demonstration");
        addLog("   💡 To test with real data:");
        addLog("      1. Run 'Search Buildings' first");
        addLog("      2. Note twin IDs from search results");
        addLog("      3. Update code to use real twin IDs");
        addLog("");
        
        // Use a sample twin ID - in practice, you'd get this from building search
        String sampleTwinId = "PNT9CnuLmTV4tkZwAigypXrnY"; // Tinsley twin ID
        // Alternative: "PNT6hrRL8shRqbwLaXsbhDrBC" for Gabaldon twin ID
        
        // Get last 24 hours
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        long now = System.currentTimeMillis();
        long yesterday = now - (24 * 60 * 60 * 1000);
        
        String startTime = isoFormat.format(new Date(yesterday));
        String endTime = isoFormat.format(new Date(now));
        
        addLog("   Time range: " + startTime + " to " + endTime);
        addLog("   Sample Twin ID: " + sampleTwinId);
        
        apiService.getTimeSeries(sampleTwinId, startTime, endTime, 100, new WillowApiV3Service.ApiCallback<List<WillowTimeSeriesData>>() {
            @Override
            public void onSuccess(List<WillowTimeSeriesData> timeSeriesData) {
                mainHandler.post(() -> {
                    addLog("✅ Time series request completed!");
                    addLog("   Retrieved " + timeSeriesData.size() + " data points");
                    addLog("");
                    
                    if (timeSeriesData.isEmpty()) {
                        addLog("⚠️ No time series data found");
                        addLog("💡 This is expected since we used a sample twin ID");
                        addLog("   To get real data:");
                        addLog("   1. Use twin IDs from the 'Search Buildings' results");
                        addLog("   2. Look for twins with model type containing 'Capability'");
                        addLog("   3. Ensure the twins have associated sensors");
                    } else {
                        addLog("📈 Sample data points:");
                        for (int i = 0; i < Math.min(5, timeSeriesData.size()); i++) {
                            WillowTimeSeriesData point = timeSeriesData.get(i);
                            addLog("   " + point.getSourceTimestamp() + ": " + point.getFormattedValue());
                        }
                        if (timeSeriesData.size() > 5) {
                            addLog("   ... and " + (timeSeriesData.size() - 5) + " more data points");
                        }
                        addLog("");
                        addLog("🎯 Success! Time series API is working correctly.");
                    }
                    
                    updateUIState(false);
                });
            }
            
            @Override
            public void onError(String error) {
                mainHandler.post(() -> {
                    addLog("❌ Time series request failed: " + error);
                    addLog("");
                    addLog("💡 This is expected since we used a sample twin ID");
                    addLog("   To test with real data:");
                    addLog("   1. Search for buildings first using '3️⃣ Search Buildings'");
                    addLog("   2. Get twin IDs from the search results");
                    addLog("   3. Look for related 'Capability' twins in relationships");
                    addLog("   4. Use those twin IDs for time series requests");
                    addLog("");
                    addLog("🔧 The API structure is correct - just needs valid twin IDs!");
                    
                    updateUIState(false);
                });
            }
        });
    }
    
    private void getModels() {
        if (!isCredentialsSet) {
            Toast.makeText(this, "Please set credentials first", Toast.LENGTH_SHORT).show();
            return;
        }
        
        updateUIState(true);
        addLog("📋 Getting available models...");
        addLog("   Using API endpoint: " + apiService.getBaseUrl() + "/models");
        
        apiService.getModels(new WillowApiV3Service.ApiCallback<List<Object>>() {
            @Override
            public void onSuccess(List<Object> models) {
                mainHandler.post(() -> {
                    addLog("✅ Models retrieved successfully!");
                    addLog("   Found " + models.size() + " model(s)");
                    addLog("");
                    
                    if (models.isEmpty()) {
                        addLog("⚠️ No models found");
                        addLog("💡 This might indicate:");
                        addLog("   • No models configured in this organization");
                        addLog("   • Insufficient permissions to view models");
                        addLog("   • API endpoint not accessible");
                    } else {
                        addLog("🏗️ Available models:");
                        for (int i = 0; i < Math.min(10, models.size()); i++) {
                            addLog("   " + (i + 1) + ". " + models.get(i).toString());
                        }
                        if (models.size() > 10) {
                            addLog("   ... and " + (models.size() - 10) + " more models");
                        }
                        addLog("");
                        addLog("🎯 Great! Models are accessible. This confirms API connectivity.");
                    }
                    
                    updateUIState(false);
                });
            }
            
            @Override
            public void onError(String error) {
                mainHandler.post(() -> {
                    addLog("❌ Failed to get models: " + error);
                    addLog("");
                    addLog("💡 Troubleshooting:");
                    if (error.contains("401") || error.contains("token")) {
                        addLog("   • Authentication issue - try 'Test Authentication' first");
                    } else if (error.contains("404")) {
                        addLog("   • Models endpoint might not exist");
                        addLog("   • Check organization URL format");
                    } else if (error.contains("403")) {
                        addLog("   • Insufficient permissions to access models");
                    } else {
                        addLog("   • Check network connectivity");
                        addLog("   • Verify API base URL is correct");
                    }
                    
                    updateUIState(false);
                });
            }
        });
    }
    
    private void updateUIState(boolean isTesting) {
        progressBar.setVisibility(isTesting ? View.VISIBLE : View.GONE);
        setCredentialsButton.setEnabled(!isTesting);
        testAuthButton.setEnabled(!isTesting && isCredentialsSet);
        searchBuildingsButton.setEnabled(!isTesting && isCredentialsSet);
        getTimeSeriesButton.setEnabled(!isTesting && isCredentialsSet);
        getModelsButton.setEnabled(!isTesting && isCredentialsSet);
        
        String status;
        if (isTesting) {
            status = "Testing in progress...";
        } else if (isCredentialsSet) {
            status = "Ready to test API v3";
        } else {
            status = "Set credentials to begin";
        }
        statusText.setText(status);
    }
    
    private void addLog(String message) {
        Log.d(TAG, message);
        logMessages.add(getCurrentTimestamp() + " " + message);
        logsAdapter.notifyItemInserted(logMessages.size() - 1);
        logsRecyclerView.scrollToPosition(logMessages.size() - 1);
    }
    
    private String getCurrentTimestamp() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
        return timeFormat.format(new Date());
    }
    
    private boolean isValidOrganizationInput(String organization) {
        if (organization == null || organization.trim().isEmpty()) {
            return false;
        }
        
        String trimmed = organization.trim();
        
        // Check if it's a full URL
        if (trimmed.startsWith("https://") && trimmed.contains(".app.willowinc.com")) {
            return true;
        }
        
        // Check if it's just an organization name
        return WillowApiV3Config.isValidOrganization(trimmed);
    }
}
