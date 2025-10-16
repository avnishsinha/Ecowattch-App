package com.example.ecowattchtechdemo.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.ecowattchtechdemo.api.WillowApiV3Service;
import com.example.ecowattchtechdemo.config.WillowApiV3Config;
import com.example.ecowattchtechdemo.models.DormEnergyData;
import com.example.ecowattchtechdemo.models.UserData;
import com.example.ecowattchtechdemo.models.WillowTimeSeriesData;
import com.example.ecowattchtechdemo.models.WillowTwinV3;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Repository class to handle all data operations for the EcoWattch app
 * Connects Willow API data with UI components
 */
public class EcoWattchRepository {
    private static final String TAG = "EcoWattchRepository";
    private static final String PREFS_NAME = "ecowattch_prefs";
    private static final String KEY_SELECTED_DORM = "selected_dorm";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_ENERGY_POINTS = "energy_points";
    
    private final WillowApiV3Service apiService;
    private final SharedPreferences preferences;
    private final Context context;
    
    // Known dorms with their Willow twin IDs
    private final Map<String, String> dormTwinIds;
    
    // Cached data
    private List<DormEnergyData> dormDataCache;
    private UserData currentUser;
    private long lastCacheUpdate = 0;
    private static final long CACHE_DURATION = 30 * 60 * 1000; // 30 minutes
    
    public EcoWattchRepository(Context context) {
        this.context = context;
        this.apiService = new WillowApiV3Service();
        this.preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        
        // Initialize known dorm twin IDs
        dormTwinIds = new HashMap<>();
        dormTwinIds.put("TINSLEY", WillowApiV3Config.TINSLEY_TWIN_ID);
        dormTwinIds.put("GABALDON", WillowApiV3Config.GABALDON_TWIN_ID);
        // Add more dorms as they become available
        
        // Initialize current user
        loadUserFromPreferences();
        
        Log.d(TAG, "Repository initialized with " + dormTwinIds.size() + " known dorms");
    }
    
    /**
     * Initialize API service with credentials
     */
    public void initializeApi(String organization, String clientId, String clientSecret) {
        apiService.setOrganization(organization);
        apiService.setCredentials(clientId, clientSecret);
        Log.d(TAG, "API service initialized for organization: " + organization);
    }
    
    /**
     * Get current user data
     */
    public UserData getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Update user preferences
     */
    public void updateUser(String username, String selectedDorm) {
        if (currentUser == null) {
            currentUser = new UserData();
        }
        currentUser.setUsername(username);
        currentUser.setSelectedDorm(selectedDorm);
        saveUserToPreferences();
        Log.d(TAG, "User updated: " + username + " -> " + selectedDorm);
    }
    
    /**
     * Get all dorm energy data with leaderboard rankings
     */
    public void getAllDormData(DataCallback<List<DormEnergyData>> callback) {
        // Check cache first
        if (isCacheValid()) {
            Log.d(TAG, "Returning cached dorm data");
            callback.onSuccess(dormDataCache);
            return;
        }
        
        Log.d(TAG, "Fetching fresh dorm data from API");
        dormDataCache = new ArrayList<>();
        
        // Fetch data for each known dorm
        fetchDormDataSequentially(new ArrayList<>(dormTwinIds.keySet()), 0, callback);
    }
    
    /**
     * Get energy data for user's selected dorm
     */
    public void getUserDormData(DataCallback<DormEnergyData> callback) {
        if (currentUser == null || currentUser.getSelectedDorm() == null) {
            callback.onError("No dorm selected");
            return;
        }
        
        getAllDormData(new DataCallback<List<DormEnergyData>>() {
            @Override
            public void onSuccess(List<DormEnergyData> dormList) {
                for (DormEnergyData dorm : dormList) {
                    if (dorm.getDormName().equals(currentUser.getSelectedDorm())) {
                        dorm.setUserDorm(true);
                        callback.onSuccess(dorm);
                        return;
                    }
                }
                callback.onError("User's dorm not found in data");
            }
            
            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }
    
    /**
     * Get leaderboard (top 3 dorms)
     */
    public void getLeaderboard(DataCallback<List<DormEnergyData>> callback) {
        getAllDormData(new DataCallback<List<DormEnergyData>>() {
            @Override
            public void onSuccess(List<DormEnergyData> dormList) {
                // Sort by potential energy points (descending)
                Collections.sort(dormList, (a, b) -> 
                    Integer.compare(b.getPotentialEnergyPoints(), a.getPotentialEnergyPoints()));
                
                // Assign rankings
                for (int i = 0; i < dormList.size(); i++) {
                    dormList.get(i).setCurrentRank(i + 1);
                }
                
                // Return top 3
                List<DormEnergyData> top3 = dormList.subList(0, Math.min(3, dormList.size()));
                callback.onSuccess(top3);
            }
            
            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }
    
    /**
     * Update rally days left
     */
    public void updateRallyDaysLeft() {
        // Calculate days left in current 2-week rally
        // This could be enhanced to use a backend API for rally schedule
        Calendar now = Calendar.getInstance();
        Calendar rallyEnd = Calendar.getInstance();
        rallyEnd.set(2025, Calendar.OCTOBER, 29); // Example rally end date
        
        long diffMillis = rallyEnd.getTimeInMillis() - now.getTimeInMillis();
        int daysLeft = (int) (diffMillis / (24 * 60 * 60 * 1000));
        
        if (currentUser != null) {
            currentUser.setCurrentRallyDaysLeft(Math.max(0, daysLeft));
            saveUserToPreferences();
        }
    }
    
    // Private helper methods
    
    private void fetchDormDataSequentially(List<String> dormNames, int index, 
                                         DataCallback<List<DormEnergyData>> callback) {
        if (index >= dormNames.size()) {
            // All dorms processed
            lastCacheUpdate = System.currentTimeMillis();
            Log.d(TAG, "All dorm data fetched successfully: " + dormDataCache.size() + " dorms");
            callback.onSuccess(dormDataCache);
            return;
        }
        
        String dormName = dormNames.get(index);
        String twinId = dormTwinIds.get(dormName);
        
        if (twinId == null) {
            Log.w(TAG, "No twin ID found for dorm: " + dormName);
            fetchDormDataSequentially(dormNames, index + 1, callback);
            return;
        }
        
        fetchSingleDormData(dormName, twinId, new DataCallback<DormEnergyData>() {
            @Override
            public void onSuccess(DormEnergyData dormData) {
                dormDataCache.add(dormData);
                fetchDormDataSequentially(dormNames, index + 1, callback);
            }
            
            @Override
            public void onError(String error) {
                Log.w(TAG, "Failed to fetch data for " + dormName + ": " + error);
                // Add placeholder data so UI doesn't break
                DormEnergyData placeholder = createPlaceholderDormData(dormName, twinId);
                dormDataCache.add(placeholder);
                fetchDormDataSequentially(dormNames, index + 1, callback);
            }
        });
    }
    
    private void fetchSingleDormData(String dormName, String twinId, DataCallback<DormEnergyData> callback) {
        Log.d(TAG, "Fetching data for " + dormName + " (Twin ID: " + twinId + ")");
        
        DormEnergyData dormData = new DormEnergyData(dormName, twinId);
        
        // Get latest time series data for current energy load
        apiService.getLatestTimeSeries(twinId, new WillowApiV3Service.ApiCallback<List<WillowTimeSeriesData>>() {
            @Override
            public void onSuccess(List<WillowTimeSeriesData> timeSeriesData) {
                // Process time series data
                if (timeSeriesData != null && !timeSeriesData.isEmpty()) {
                    WillowTimeSeriesData latest = timeSeriesData.get(0);
                    if (latest.getScalarValue() != 0) {
                        dormData.setCurrentEnergyLoad(latest.getScalarValue());
                    }
                }
                
                // Calculate yesterday's total (simplified calculation)
                dormData.setYesterdayTotal(dormData.getCurrentEnergyLoad() * 24 * 0.8); // Rough estimate
                
                // Calculate potential energy points based on energy efficiency
                int points = calculatePotentialEnergyPoints(dormData.getCurrentEnergyLoad());
                dormData.setPotentialEnergyPoints(points);
                
                Log.d(TAG, "Successfully fetched data for " + dormName + 
                          ": " + dormData.getFormattedEnergyLoad());
                callback.onSuccess(dormData);
            }
            
            @Override
            public void onError(String error) {
                Log.e(TAG, "Failed to fetch time series for " + dormName + ": " + error);
                callback.onError(error);
            }
        });
    }
    
    private DormEnergyData createPlaceholderDormData(String dormName, String twinId) {
        DormEnergyData placeholder = new DormEnergyData(dormName, twinId);
        // Set reasonable default values
        placeholder.setCurrentEnergyLoad(250.0 + Math.random() * 100); // 250-350 kW
        placeholder.setYesterdayTotal(5000.0 + Math.random() * 2000); // 5000-7000 kWh
        placeholder.setPotentialEnergyPoints((int)(200 + Math.random() * 100)); // 200-300 points
        return placeholder;
    }
    
    private int calculatePotentialEnergyPoints(double currentLoad) {
        // Simple algorithm: lower energy usage = more points
        // This could be enhanced with historical data and efficiency targets
        if (currentLoad < 200) {
            return 300; // Excellent efficiency
        } else if (currentLoad < 280) {
            return 250; // Good efficiency
        } else if (currentLoad < 350) {
            return 200; // Average efficiency
        } else {
            return 150; // Poor efficiency
        }
    }
    
    private boolean isCacheValid() {
        return dormDataCache != null && 
               !dormDataCache.isEmpty() && 
               (System.currentTimeMillis() - lastCacheUpdate) < CACHE_DURATION;
    }
    
    private void loadUserFromPreferences() {
        String username = preferences.getString(KEY_USERNAME, null);
        String selectedDorm = preferences.getString(KEY_SELECTED_DORM, null);
        int energyPoints = preferences.getInt(KEY_ENERGY_POINTS, 5460); // Default value
        
        if (username != null) {
            currentUser = new UserData(username, selectedDorm);
            currentUser.setSpendableEnergyPoints(energyPoints);
            updateRallyDaysLeft();
        }
    }
    
    private void saveUserToPreferences() {
        if (currentUser != null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(KEY_USERNAME, currentUser.getUsername());
            editor.putString(KEY_SELECTED_DORM, currentUser.getSelectedDorm());
            editor.putInt(KEY_ENERGY_POINTS, currentUser.getSpendableEnergyPoints());
            editor.apply();
        }
    }
    
    /**
     * Clear cache to force refresh on next request
     */
    public void clearCache() {
        dormDataCache = null;
        lastCacheUpdate = 0;
        Log.d(TAG, "Cache cleared");
    }
    
    /**
     * Callback interface for async data operations
     */
    public interface DataCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }
}
