package com.example.ecowattchtechdemo.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ecowattchtechdemo.config.WillowApiV3Config;
import com.example.ecowattchtechdemo.models.DormEnergyData;
import com.example.ecowattchtechdemo.models.UserData;
import com.example.ecowattchtechdemo.repository.EcoWattchRepository;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * ViewModel for the main dashboard, manages all UI data and API interactions
 */
public class DashboardViewModel extends AndroidViewModel {
    private static final String TAG = "DashboardViewModel";
    
    private final EcoWattchRepository repository;
    private final ScheduledExecutorService scheduler;
    
    // LiveData for UI components
    private final MutableLiveData<DormEnergyData> userDormData = new MutableLiveData<>();
    private final MutableLiveData<List<DormEnergyData>> leaderboardData = new MutableLiveData<>();
    private final MutableLiveData<UserData> userData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isApiConnected = new MutableLiveData<>();
    
    public DashboardViewModel(@NonNull Application application) {
        super(application);
        this.repository = new EcoWattchRepository(application);
        this.scheduler = Executors.newScheduledThreadPool(2);
        
        // Initialize with default API configuration
        initializeApiWithDefaults();
        
        // Load initial data
        loadUserData();
        startDataRefreshScheduler();
        
        Log.d(TAG, "DashboardViewModel initialized");
    }
    
    // LiveData getters
    public LiveData<DormEnergyData> getUserDormData() {
        return userDormData;
    }
    
    public LiveData<List<DormEnergyData>> getLeaderboardData() {
        return leaderboardData;
    }
    
    public LiveData<UserData> getUserData() {
        return userData;
    }
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    public LiveData<Boolean> getIsApiConnected() {
        return isApiConnected;
    }
    
    /**
     * Initialize API with default configuration.
     * TODO: Replace with environment variables from .env file
     * See .env_example for configuration template
     */
    private void initializeApiWithDefaults() {
        try {
            repository.initializeApi(
                WillowApiV3Config.DEFAULT_BASE_URL,
                "YOUR_CLIENT_ID_HERE", // TODO: Load from .env file
                "YOUR_CLIENT_SECRET_HERE"  // TODO: Load from .env file
            );
            isApiConnected.postValue(true);
            Log.d(TAG, "API initialized with default credentials");
        } catch (Exception e) {
            isApiConnected.postValue(false);
            Log.e(TAG, "Failed to initialize API with default credentials", e);
        }
    }

    /**
     * Update user information
     */
    public void updateUser(String username, String selectedDorm) {
        repository.updateUser(username, selectedDorm);
        loadUserData();
        refreshAllData();
    }
    
    /**
     * Load user data from repository
     */
    private void loadUserData() {
        UserData user = repository.getCurrentUser();
        if (user == null) {
            // Create default user
            user = new UserData("Guest", "TINSLEY");
            user.setSpendableEnergyPoints(5460);
            user.setCurrentRallyDaysLeft(3);
            repository.updateUser(user.getUsername(), user.getSelectedDorm());
        }
        userData.postValue(user);
        Log.d(TAG, "User data loaded: " + user.getUsername());
    }
    
    /**
     * Refresh all dashboard data
     */
    public void refreshAllData() {
        Log.d(TAG, "Refreshing all dashboard data");
        isLoading.postValue(true);
        
        // Clear cache to force fresh data
        repository.clearCache();
        
        // Load user's dorm data
        loadUserDormData();
        
        // Load leaderboard data
        loadLeaderboardData();
    }
    
    /**
     * Load energy data for user's selected dorm
     */
    private void loadUserDormData() {
        repository.getUserDormData(new EcoWattchRepository.DataCallback<DormEnergyData>() {
            @Override
            public void onSuccess(DormEnergyData dormData) {
                userDormData.postValue(dormData);
                Log.d(TAG, "User dorm data loaded: " + dormData.getDormName() + 
                          " - " + dormData.getFormattedEnergyLoad());
            }
            
            @Override
            public void onError(String error) {
                Log.e(TAG, "Failed to load user dorm data: " + error);
                errorMessage.postValue("Failed to load dorm data: " + error);
                
                // Provide fallback data
                UserData user = userData.getValue();
                if (user != null && user.getSelectedDorm() != null) {
                    DormEnergyData fallback = createFallbackDormData(user.getSelectedDorm());
                    userDormData.postValue(fallback);
                }
            }
        });
    }
    
    /**
     * Load leaderboard data (top 3 dorms)
     */
    private void loadLeaderboardData() {
        repository.getLeaderboard(new EcoWattchRepository.DataCallback<List<DormEnergyData>>() {
            @Override
            public void onSuccess(List<DormEnergyData> leaderboard) {
                leaderboardData.postValue(leaderboard);
                isLoading.postValue(false);
                Log.d(TAG, "Leaderboard data loaded: " + leaderboard.size() + " dorms");
            }
            
            @Override
            public void onError(String error) {
                Log.e(TAG, "Failed to load leaderboard data: " + error);
                isLoading.postValue(false);
                errorMessage.postValue("Failed to load leaderboard: " + error);
            }
        });
    }
    
    /**
     * Start scheduled data refresh every 30 minutes
     */
    private void startDataRefreshScheduler() {
        scheduler.scheduleAtFixedRate(() -> {
            Log.d(TAG, "Scheduled data refresh");
            refreshAllData();
        }, 30, 30, TimeUnit.MINUTES);
    }
    
    /**
     * Create fallback data when API fails
     */
    private DormEnergyData createFallbackDormData(String dormName) {
        DormEnergyData fallback = new DormEnergyData(dormName, "");
        fallback.setCurrentEnergyLoad(280.0);
        fallback.setYesterdayTotal(5297.0);
        fallback.setPotentialEnergyPoints(237);
        fallback.setCurrentRank(1);
        fallback.setUserDorm(true);
        Log.d(TAG, "Created fallback data for " + dormName);
        return fallback;
    }
    
    /**
     * Test API connectivity
     */
    public void testApiConnection() {
        isLoading.postValue(true);
        
        // Try to fetch data to test connection
        repository.getAllDormData(new EcoWattchRepository.DataCallback<List<DormEnergyData>>() {
            @Override
            public void onSuccess(List<DormEnergyData> dormList) {
                isApiConnected.postValue(true);
                isLoading.postValue(false);
                Log.d(TAG, "API connection test successful");
            }
            
            @Override
            public void onError(String error) {
                isApiConnected.postValue(false);
                isLoading.postValue(false);
                errorMessage.postValue("API connection failed: " + error);
                Log.e(TAG, "API connection test failed: " + error);
            }
        });
    }
    
    /**
     * Clear error message
     */
    public void clearError() {
        errorMessage.postValue(null);
    }
    
    /**
     * Force data refresh now
     */
    public void forceRefresh() {
        Log.d(TAG, "Force refresh requested");
        refreshAllData();
    }
    
    @Override
    protected void onCleared() {
        super.onCleared();
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
        Log.d(TAG, "DashboardViewModel cleared");
    }
}
