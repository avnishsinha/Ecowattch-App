package com.example.ecowattchtechdemo.willow;

import android.util.Log;
import com.example.ecowattchtechdemo.willow.models.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.*;

/**
 * Manager for Willow API operations and energy data
 */
public class WillowEnergyDataManager {
    
    private static final String TAG = "WillowEnergyManager";
    
    private WillowApiService apiService;
    private String accessToken;
    private long tokenExpirationTime;
    
    // Building mappings
    private final Map<String, String> buildingNames = new HashMap<String, String>() {{
        put(WillowApiV3Config.TWIN_ID_TINSLEY, "TINSLEY");
        put(WillowApiV3Config.TWIN_ID_GABALDON, "GABALDON");
        put(WillowApiV3Config.TWIN_ID_SECHRIST, "SECHRIST");
    }};
    
    private final Map<String, String> buildingPositions = new HashMap<String, String>() {{
        put("TINSLEY", "1ST PLACE");
        put("GABALDON", "2ND PLACE");
        put("SECHRIST", "3RD PLACE");
    }};
    
    public WillowEnergyDataManager() {
        this.apiService = WillowApiClient.getApiService();
    }
    
    public WillowEnergyDataManager(String baseUrl) {
        this.apiService = WillowApiClient.getApiService(baseUrl);
    }
    
    /**
     * Interface for energy data callbacks
     */
    public interface EnergyDataCallback {
        void onSuccess(EnergyDataResponse data);
        void onError(String error);
    }
    
    public interface AuthenticationCallback {
        void onSuccess(String token);
        void onError(String error);
    }
    
    /**
     * Authenticate with Willow API
     */
    public void authenticate(String clientId, String clientSecret, AuthenticationCallback callback) {
        Log.d(TAG, "Authenticating with Willow API...");
        
        Call<WillowOAuthResponse> call = apiService.getOAuthToken(
            clientId, 
            clientSecret, 
            WillowApiV3Config.GRANT_TYPE
        );
        
        call.enqueue(new Callback<WillowOAuthResponse>() {
            @Override
            public void onResponse(Call<WillowOAuthResponse> call, Response<WillowOAuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WillowOAuthResponse tokenResponse = response.body();
                    
                    if (tokenResponse.isValid()) {
                        accessToken = tokenResponse.getAuthorizationHeader();
                        tokenExpirationTime = System.currentTimeMillis() + (tokenResponse.getExpiresIn() * 1000L);
                        
                        Log.d(TAG, "Authentication successful");
                        callback.onSuccess(accessToken);
                    } else {
                        Log.e(TAG, "Invalid token response");
                        callback.onError("Invalid authentication response");
                    }
                } else {
                    String error = "Authentication failed: " + response.code();
                    Log.e(TAG, error);
                    callback.onError(error);
                }
            }
            
            @Override
            public void onFailure(Call<WillowOAuthResponse> call, Throwable t) {
                String error = "Authentication network error: " + t.getMessage();
                Log.e(TAG, error, t);
                callback.onError(error);
            }
        });
    }
    
    /**
     * Get energy data for a specific building
     */
    public void getEnergyData(String buildingTwinId, EnergyDataCallback callback) {
        if (!isAuthenticated()) {
            callback.onError("Not authenticated. Please authenticate first.");
            return;
        }
        
        final String buildingName = buildingNames.getOrDefault(buildingTwinId, "UNKNOWN");
        
        Log.d(TAG, "Fetching energy data for building: " + buildingName + " (" + buildingTwinId + ")");
        
        // Use the new debugging approach to handle 500 errors better
        testApiEndpoints(buildingTwinId, callback);
    }
    
    /**
     * Find energy capability twins for the building - Simplified approach to avoid 500 errors
     */
    private void findEnergyCapabilities(DigitalTwin building, String buildingName, EnergyDataCallback callback) {
        Log.d(TAG, "Searching for energy capabilities using simplified approach");
        
        // Try a simpler search first - just search for energy model types
        Map<String, Object> searchRequest = new HashMap<>();
        
        // Simplified model filter - search one model at a time to avoid complex queries
        Map<String, Object> modelFilter = new HashMap<>();
        modelFilter.put("modelIds", Arrays.asList(WillowApiV3Config.ENERGY_CONSUMPTION_MODEL));
        modelFilter.put("exactModelMatch", false);
        
        searchRequest.put("modelFilter", modelFilter);
        searchRequest.put("pageSize", 20); // Reduced page size
        
        Call<TwinsResponse> searchCall = apiService.searchTwins(accessToken, searchRequest);
        
        searchCall.enqueue(new Callback<TwinsResponse>() {
            @Override
            public void onResponse(Call<TwinsResponse> call, Response<TwinsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TwinsResponse twinsResponse = response.body();
                    
                    if (twinsResponse.hasTwins()) {
                        Log.d(TAG, "Found " + twinsResponse.getContent().size() + " energy capabilities");
                        
                        // Filter capabilities that might be related to our building
                        List<String> capabilityIds = new ArrayList<>();
                        for (DigitalTwin twin : twinsResponse.getContent()) {
                            // Simple filtering - include all found capabilities for now
                            capabilityIds.add(twin.getId());
                            Log.d(TAG, "Found energy capability: " + twin.getName() + " (" + twin.getId() + ")");
                        }
                        
                        if (!capabilityIds.isEmpty()) {
                            getTimeSeriesData(capabilityIds, buildingName, building.getId(), callback);
                        } else {
                            Log.w(TAG, "No valid energy capabilities found");
                            EnergyDataResponse fallbackData = createFallbackData(buildingName, building.getId());
                            callback.onSuccess(fallbackData);
                        }
                    } else {
                        Log.w(TAG, "No energy capabilities found in response");
                        // Try alternative approach - search for power consumption model
                        searchForAlternativeCapabilities(buildingName, building.getId(), callback);
                    }
                } else {
                    String errorMsg = "Failed to search energy capabilities: " + response.code();
                    if (response.errorBody() != null) {
                        try {
                            errorMsg += " - " + response.errorBody().string();
                        } catch (Exception e) {
                            errorMsg += " (could not read error body)";
                        }
                    }
                    Log.e(TAG, errorMsg);
                    
                    // Try alternative approach before falling back
                    searchForAlternativeCapabilities(buildingName, building.getId(), callback);
                }
            }
            
            @Override
            public void onFailure(Call<TwinsResponse> call, Throwable t) {
                Log.e(TAG, "Network error searching capabilities: " + t.getMessage(), t);
                // Try alternative approach before falling back
                searchForAlternativeCapabilities(buildingName, building.getId(), callback);
            }
        });
    }
    
    /**
     * Alternative search approach using different model types
     */
    private void searchForAlternativeCapabilities(String buildingName, String buildingId, EnergyDataCallback callback) {
        Log.d(TAG, "Trying alternative search for power consumption capabilities");
        
        Map<String, Object> searchRequest = new HashMap<>();
        Map<String, Object> modelFilter = new HashMap<>();
        modelFilter.put("modelIds", Arrays.asList(WillowApiV3Config.POWER_CONSUMPTION_MODEL));
        modelFilter.put("exactModelMatch", false);
        
        searchRequest.put("modelFilter", modelFilter);
        searchRequest.put("pageSize", 20);
        
        Call<TwinsResponse> searchCall = apiService.searchTwins(accessToken, searchRequest);
        
        searchCall.enqueue(new Callback<TwinsResponse>() {
            @Override
            public void onResponse(Call<TwinsResponse> call, Response<TwinsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().hasTwins()) {
                    TwinsResponse twinsResponse = response.body();
                    Log.d(TAG, "Found " + twinsResponse.getContent().size() + " power capabilities");
                    
                    List<String> capabilityIds = new ArrayList<>();
                    for (DigitalTwin twin : twinsResponse.getContent()) {
                        capabilityIds.add(twin.getId());
                        Log.d(TAG, "Found power capability: " + twin.getName() + " (" + twin.getId() + ")");
                    }
                    
                    getTimeSeriesData(capabilityIds, buildingName, buildingId, callback);
                } else {
                    Log.w(TAG, "Alternative search also failed, trying direct building twin approach");
                    // Try direct approach with the building twin ID
                    tryDirectBuildingApproach(buildingName, buildingId, callback);
                }
            }
            
            @Override
            public void onFailure(Call<TwinsResponse> call, Throwable t) {
                Log.e(TAG, "Alternative search failed: " + t.getMessage(), t);
                // Try direct approach as last resort
                tryDirectBuildingApproach(buildingName, buildingId, callback);
            }
        });
    }
    
    /**
     * Try to get time series data directly from the building twin
     */
    private void tryDirectBuildingApproach(String buildingName, String buildingId, EnergyDataCallback callback) {
        Log.d(TAG, "Attempting direct building twin approach for time series data");
        
        // Try to get time series data directly using the building twin ID
        List<String> directIds = Arrays.asList(buildingId);
        
        Call<List<TimeSeriesPoint>> directCall = apiService.getLatestTimeSeriesValues(
            accessToken, directIds, true
        );
        
        directCall.enqueue(new Callback<List<TimeSeriesPoint>>() {
            @Override
            public void onResponse(Call<List<TimeSeriesPoint>> call, Response<List<TimeSeriesPoint>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Log.d(TAG, "Direct approach succeeded! Found time series data");
                    List<TimeSeriesPoint> timeSeriesData = response.body();
                    EnergyDataResponse energyData = processTimeSeriesData(timeSeriesData, buildingName, buildingId);
                    callback.onSuccess(energyData);
                } else {
                    Log.w(TAG, "Direct approach failed, using fallback data. Response code: " + response.code());
                    EnergyDataResponse fallbackData = createFallbackData(buildingName, buildingId);
                    callback.onSuccess(fallbackData);
                }
            }
            
            @Override
            public void onFailure(Call<List<TimeSeriesPoint>> call, Throwable t) {
                Log.e(TAG, "Direct approach network error: " + t.getMessage(), t);
                EnergyDataResponse fallbackData = createFallbackData(buildingName, buildingId);
                callback.onSuccess(fallbackData);
            }
        });
    }
    
    /**
     * Get time series data for energy capabilities
     */
    private void getTimeSeriesData(List<String> capabilityIds, String buildingName, String buildingId, EnergyDataCallback callback) {
        if (capabilityIds.isEmpty()) {
            Log.w(TAG, "No capability IDs provided for time series data");
            EnergyDataResponse fallbackData = createFallbackData(buildingName, buildingId);
            callback.onSuccess(fallbackData);
            return;
        }
        
        Log.d(TAG, "Requesting time series data for " + capabilityIds.size() + " capabilities");
        
        Call<List<TimeSeriesPoint>> latestCall = apiService.getLatestTimeSeriesValues(
            accessToken, capabilityIds, true
        );
        
        latestCall.enqueue(new Callback<List<TimeSeriesPoint>>() {
            @Override
            public void onResponse(Call<List<TimeSeriesPoint>> call, Response<List<TimeSeriesPoint>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TimeSeriesPoint> timeSeriesData = response.body();
                    
                    if (!timeSeriesData.isEmpty()) {
                        Log.d(TAG, "Retrieved " + timeSeriesData.size() + " time series points");
                        
                        // Log first few points for debugging
                        for (int i = 0; i < Math.min(3, timeSeriesData.size()); i++) {
                            TimeSeriesPoint point = timeSeriesData.get(i);
                            Log.d(TAG, "Time series point " + i + ": " + 
                                 point.getScalarValue() + " at " + point.getSourceTimestamp());
                        }
                        
                        // Process the real data
                        EnergyDataResponse energyData = processTimeSeriesData(
                            timeSeriesData, buildingName, buildingId
                        );
                        callback.onSuccess(energyData);
                    } else {
                        Log.w(TAG, "Time series response was empty");
                        EnergyDataResponse fallbackData = createFallbackData(buildingName, buildingId);
                        callback.onSuccess(fallbackData);
                    }
                } else {
                    String errorMsg = "Failed to get time series data: " + response.code();
                    if (response.errorBody() != null) {
                        try {
                            errorMsg += " - " + response.errorBody().string();
                        } catch (Exception e) {
                            errorMsg += " (could not read error body)";
                        }
                    }
                    Log.e(TAG, errorMsg);
                    EnergyDataResponse fallbackData = createFallbackData(buildingName, buildingId);
                    callback.onSuccess(fallbackData);
                }
            }
            
            @Override
            public void onFailure(Call<List<TimeSeriesPoint>> call, Throwable t) {
                Log.e(TAG, "Network error getting time series: " + t.getMessage(), t);
                EnergyDataResponse fallbackData = createFallbackData(buildingName, buildingId);
                callback.onSuccess(fallbackData);
            }
        });
    }
    
    /**
     * Process time series data into energy response
     */
    private EnergyDataResponse processTimeSeriesData(List<TimeSeriesPoint> timeSeriesData, 
                                                   String buildingName, String buildingId) {
        EnergyDataResponse energyData = new EnergyDataResponse(buildingName, buildingId);
        
        // Find the most recent valid energy reading
        TimeSeriesPoint latestPoint = null;
        double totalEnergy = 0.0;
        int validPointCount = 0;
        
        for (TimeSeriesPoint point : timeSeriesData) {
            if (point.getScalarValue() != null && 
                (point.getDataQuality() == null || point.getDataQuality().isGoodQuality())) {
                
                if (latestPoint == null || 
                    point.getSourceTimestamp().compareTo(latestPoint.getSourceTimestamp()) > 0) {
                    latestPoint = point;
                }
                
                totalEnergy += point.getScalarValue();
                validPointCount++;
            }
        }
        
        if (latestPoint != null) {
            // Set real data
            energyData.setCurrentUsageKW(latestPoint.getValueAsKW());
            energyData.setDailyTotalKWh(totalEnergy);
            energyData.setLastUpdated(latestPoint.getSourceTimestamp());
            energyData.setDataAvailable(true);
            energyData.setStatus("Live Data");
            
            // Calculate potential energy
            energyData.calculatePotentialEnergy();
            
            Log.d(TAG, "Processed real energy data: " + energyData.getCurrentUsageKW() + " kW");
        } else {
            Log.w(TAG, "No valid time series data found, using fallback");
            return createFallbackData(buildingName, buildingId);
        }
        
        return energyData;
    }
    
    /**
     * Create fallback data when real data is not available
     */
    private EnergyDataResponse createFallbackData(String buildingName, String buildingId) {
        EnergyDataResponse fallbackData = new EnergyDataResponse(buildingName, buildingId);
        
        // Use simulated values similar to original logic but mark as fallback
        int baseUsage = getBaseUsageForBuilding(buildingName);
        Random random = new Random();
        int currentUsage = baseUsage + random.nextInt(50) - 25; // ±25kW variation
        
        fallbackData.setCurrentUsageKW((double) currentUsage);
        fallbackData.setDailyTotalKWh((double) (currentUsage * 24 + random.nextInt(1000)));
        fallbackData.calculatePotentialEnergy();
        fallbackData.setLastUpdated(new Date().toString());
        fallbackData.setDataAvailable(false);
        fallbackData.setStatus("Simulated Data");
        
        Log.d(TAG, "Using fallback data for " + buildingName + ": " + currentUsage + " kW");
        
        return fallbackData;
    }
    
    /**
     * Get base usage for building (dynamic calculation)
     */
    private int getBaseUsageForBuilding(String buildingName) {
        // Dynamic calculation based on time of day and building characteristics
        int timeOfDayMultiplier = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY);
        int baseLoad = 200 + (timeOfDayMultiplier * 5); // Base load varies with time
        
        // Building efficiency factor (based on actual building characteristics)
        double efficiencyFactor = 1.0;
        switch (buildingName.toUpperCase()) {
            case "TINSLEY": efficiencyFactor = 0.85; break; // Newer building, more efficient
            case "GABALDON": efficiencyFactor = 1.0; break; // Average efficiency
            case "SECHRIST": efficiencyFactor = 1.15; break; // Older building, less efficient
            default: efficiencyFactor = 1.0; break;
        }
        
        return (int) (baseLoad * efficiencyFactor);
    }
    
    /**
     * Get building position for leaderboard
     */
    public String getBuildingPosition(String buildingName) {
        return buildingPositions.get(buildingName.toUpperCase());
    }
    
    /**
     * Check if currently authenticated
     */
    public boolean isAuthenticated() {
        return accessToken != null && System.currentTimeMillis() < tokenExpirationTime;
    }
    
    /**
     * Test method to debug API issues
     */
    public void testApiEndpoints(String buildingTwinId, EnergyDataCallback callback) {
        if (!isAuthenticated()) {
            callback.onError("Not authenticated");
            return;
        }
        
        Log.d(TAG, "🔧 DEBUGGING: Testing API endpoints for twin ID: " + buildingTwinId);
        
        // Test 1: Try to get the building twin directly
        Call<DigitalTwin> twinCall = apiService.getTwinById(accessToken, buildingTwinId, true);
        
        twinCall.enqueue(new Callback<DigitalTwin>() {
            @Override
            public void onResponse(Call<DigitalTwin> call, Response<DigitalTwin> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DigitalTwin building = response.body();
                    Log.d(TAG, "✅ Building twin found: " + building.getName() + " (Model: " + building.getModelId() + ")");
                    
                    // Test 2: Try direct time series call
                    testDirectTimeSeries(building, callback);
                } else {
                    Log.e(TAG, "❌ Failed to get building twin: " + response.code());
                    callback.onError("Failed to get building twin: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<DigitalTwin> call, Throwable t) {
                Log.e(TAG, "❌ Network error getting building twin: " + t.getMessage(), t);
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
    
    /**
     * Test direct time series access
     */
    private void testDirectTimeSeries(DigitalTwin building, EnergyDataCallback callback) {
        Log.d(TAG, "🔧 DEBUGGING: Testing direct time series access");
        
        Call<List<TimeSeriesPoint>> directCall = apiService.getLatestTimeSeries(
            accessToken, building.getId(), true
        );
        
        directCall.enqueue(new Callback<List<TimeSeriesPoint>>() {
            @Override
            public void onResponse(Call<List<TimeSeriesPoint>> call, Response<List<TimeSeriesPoint>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TimeSeriesPoint> points = response.body();
                    Log.d(TAG, "✅ Direct time series success: " + points.size() + " points found");
                    
                    if (!points.isEmpty()) {
                        // Process and return real data
                        String buildingName = buildingNames.getOrDefault(building.getId(), building.getName());
                        EnergyDataResponse energyData = processTimeSeriesData(points, buildingName, building.getId());
                        callback.onSuccess(energyData);
                    } else {
                        Log.w(TAG, "⚠️ No time series points found");
                        String buildingName = buildingNames.getOrDefault(building.getId(), building.getName());
                        EnergyDataResponse fallbackData = createFallbackData(buildingName, building.getId());
                        callback.onSuccess(fallbackData);
                    }
                } else {
                    Log.e(TAG, "❌ Direct time series failed: " + response.code());
                    // Try the simplified search approach
                    String buildingName = buildingNames.getOrDefault(building.getId(), building.getName());
                    findEnergyCapabilities(building, buildingName, callback);
                }
            }
            
            @Override
            public void onFailure(Call<List<TimeSeriesPoint>> call, Throwable t) {
                Log.e(TAG, "❌ Direct time series network error: " + t.getMessage(), t);
                // Try the simplified search approach
                String buildingName = buildingNames.getOrDefault(building.getId(), building.getName());
                findEnergyCapabilities(building, buildingName, callback);
            }
        });
    }
    
    /**
     * Get all building twin IDs
     */
    public List<String> getAllBuildingIds() {
        return new ArrayList<>(buildingNames.keySet());
    }
    
    /**
     * Get building name by ID
     */
    public String getBuildingName(String twinId) {
        return buildingNames.get(twinId);
    }
}
