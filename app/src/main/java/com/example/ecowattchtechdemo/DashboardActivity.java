package com.example.ecowattchtechdemo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecowattchtechdemo.models.DormEnergyData;
import com.example.ecowattchtechdemo.models.UserData;
import com.example.ecowattchtechdemo.viewmodel.DashboardViewModel;

import java.util.List;

/**
 * Enhanced DashboardActivity that connects the beautiful frontend UI 
 * with live backend data from the Willow API
 */
public class DashboardActivity extends AppCompatActivity {
    private static final String TAG = "DashboardActivity";
    
    // ViewModels
    private DashboardViewModel viewModel;
    
    // UI Components
    private TextView dormStatusText;
    private TextView currentEnergyLoad;
    private TextView yesterdayTotalText;
    private TextView potentialEnergyText;
    private TextView rallyDaysLeft;
    private TextView userEnergyPoints;
    private TextView apiStatusText;
    private TextView errorMessage;
    private TextView usageGaugeText;
    
    private ProgressBar energyLoadProgress;
    private ProgressBar usageGauge;
    private View apiStatusDot;
    private LinearLayout leaderboardContainer;
    private TextView leaderboardLoading;
    
    // Modal components
    private LinearLayout modalOverlay;
    private TextView modalContentText;
    private ImageView tabAlerts, tabNotifications, tabSettings, tabProfile;
    
    // Navigation components
    private LinearLayout recordsButton, shopButton;
    private ImageView hamburgerMenu, refreshButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        
        initViews();
        initViewModel();
        setupClickListeners();
        observeData();
        
        Log.d(TAG, "Enhanced DashboardActivity initialized");
        
        // Get username from intent if available
        String username = getIntent().getStringExtra("username");
        if (username != null) {
            viewModel.updateUser(username, "TINSLEY"); // Default to Tinsley
        }
        
        // Start with fresh data
        viewModel.forceRefresh();
    }
    
    private void initViews() {
        // Main content views
        dormStatusText = findViewById(R.id.dorm_status_text);
        currentEnergyLoad = findViewById(R.id.current_energy_load);
        yesterdayTotalText = findViewById(R.id.yesterday_total_text);
        potentialEnergyText = findViewById(R.id.potential_energy_text);
        rallyDaysLeft = findViewById(R.id.rally_days_left);
        userEnergyPoints = findViewById(R.id.user_energy_points);
        apiStatusText = findViewById(R.id.api_status_text);
        errorMessage = findViewById(R.id.error_message);
        usageGaugeText = findViewById(R.id.usage_gauge_text);
        
        // Progress indicators
        energyLoadProgress = findViewById(R.id.energy_load_progress);
        usageGauge = findViewById(R.id.usage_gauge);
        apiStatusDot = findViewById(R.id.api_status_dot);
        
        // Containers
        leaderboardContainer = findViewById(R.id.leaderboard_container);
        leaderboardLoading = findViewById(R.id.leaderboard_loading);
        
        // Modal components
        modalOverlay = findViewById(R.id.modal_overlay);
        modalContentText = findViewById(R.id.modal_content_text);
        tabAlerts = findViewById(R.id.tab_alerts);
        tabNotifications = findViewById(R.id.tab_notifications);
        tabSettings = findViewById(R.id.tab_settings);
        tabProfile = findViewById(R.id.tab_profile);
        
        // Navigation
        recordsButton = findViewById(R.id.records_button);
        shopButton = findViewById(R.id.shop_button);
        hamburgerMenu = findViewById(R.id.hamburger_menu);
        refreshButton = findViewById(R.id.refresh_button);
    }
    
    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
    }
    
    private void setupClickListeners() {
        // Navigation buttons
        recordsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, RecordsActivity.class);
            startActivity(intent);
            Log.d(TAG, "Records button clicked");
        });
        
        shopButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ShopActivity.class);
            startActivity(intent);
            Log.d(TAG, "Shop button clicked");
        });
        
        // Hamburger menu - toggle modal
        hamburgerMenu.setOnClickListener(v -> toggleModal());
        
        // Refresh button
        refreshButton.setOnClickListener(v -> {
            Log.d(TAG, "Manual refresh triggered");
            viewModel.forceRefresh();
        });
        
        // Modal tabs
        tabAlerts.setOnClickListener(v -> showModalTab("alerts"));
        tabNotifications.setOnClickListener(v -> showModalTab("notifications"));
        tabSettings.setOnClickListener(v -> showModalTab("settings"));
        tabProfile.setOnClickListener(v -> showModalTab("profile"));
        
        // Close modal when clicking overlay
        modalOverlay.setOnClickListener(v -> hideModal());
        
        // Error message dismiss
        errorMessage.setOnClickListener(v -> viewModel.clearError());
    }
    
    private void observeData() {
        // Observe user's dorm data
        viewModel.getUserDormData().observe(this, this::updateUserDormUI);
        
        // Observe leaderboard data
        viewModel.getLeaderboardData().observe(this, this::updateLeaderboardUI);
        
        // Observe user data
        viewModel.getUserData().observe(this, this::updateUserDataUI);
        
        // Observe loading state
        viewModel.getIsLoading().observe(this, this::updateLoadingUI);
        
        // Observe API connection status
        viewModel.getIsApiConnected().observe(this, this::updateApiStatusUI);
        
        // Observe error messages
        viewModel.getErrorMessage().observe(this, this::updateErrorUI);
    }
    
    private void updateUserDormUI(DormEnergyData dormData) {
        if (dormData == null) return;
        
        Log.d(TAG, "Updating UI with dorm data: " + dormData.getDormName());
        
        // Update dorm name and rank
        String statusText = dormData.getDormName().toUpperCase() + " - " + dormData.getFormattedRank();
        dormStatusText.setText(statusText);
        
        // Update current energy load
        currentEnergyLoad.setText(dormData.getFormattedEnergyLoad());
        
        // Update yesterday's total
        yesterdayTotalText.setText(dormData.getFormattedYesterdayTotal());
        
        // Update potential energy
        potentialEnergyText.setText(dormData.getFormattedPotentialEnergy());
        
        // Update usage gauge based on energy efficiency
        int efficiency = calculateEfficiencyPercentage(dormData.getCurrentEnergyLoad());
        usageGauge.setProgress(efficiency);
        usageGaugeText.setText(getEfficiencyText(efficiency));
        
        // Update gauge color based on efficiency
        updateGaugeColor(efficiency);
    }
    
    private void updateLeaderboardUI(List<DormEnergyData> leaderboard) {
        if (leaderboard == null || leaderboard.isEmpty()) {
            leaderboardLoading.setVisibility(View.VISIBLE);
            leaderboardLoading.setText("No leaderboard data available");
            return;
        }
        
        Log.d(TAG, "Updating leaderboard with " + leaderboard.size() + " dorms");
        
        leaderboardLoading.setVisibility(View.GONE);
        leaderboardContainer.removeAllViews();
        
        // Add top 3 dorms to leaderboard
        for (int i = 0; i < Math.min(3, leaderboard.size()); i++) {
            DormEnergyData dorm = leaderboard.get(i);
            addLeaderboardItem(dorm, i + 1);
        }
    }
    
    private void addLeaderboardItem(DormEnergyData dorm, int position) {
        TextView leaderboardItem = new TextView(this);
        leaderboardItem.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        
        String positionText = getOrdinalNumber(position) + ": " + dorm.getDormName();
        leaderboardItem.setText(positionText);
        leaderboardItem.setTextColor(getColor(R.color.white));
        leaderboardItem.setTextSize(14);
        leaderboardItem.setTypeface(getResources().getFont(R.font.matrixtyp_display_bold));
        
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) leaderboardItem.getLayoutParams();
        params.bottomMargin = (int) (10 * getResources().getDisplayMetrics().density);
        leaderboardItem.setLayoutParams(params);
        
        leaderboardContainer.addView(leaderboardItem);
    }
    
    private void updateUserDataUI(UserData userData) {
        if (userData == null) return;
        
        Log.d(TAG, "Updating user data UI: " + userData.getUsername());
        
        // Update energy points in shop button
        userEnergyPoints.setText(userData.getFormattedEnergyPoints());
        
        // Update rally days left in records button
        rallyDaysLeft.setText(userData.getFormattedDaysLeft());
    }
    
    private void updateLoadingUI(Boolean isLoading) {
        if (isLoading != null && isLoading) {
            energyLoadProgress.setVisibility(View.VISIBLE);
            leaderboardLoading.setVisibility(View.VISIBLE);
            leaderboardLoading.setText("Loading leaderboard...");
        } else {
            energyLoadProgress.setVisibility(View.GONE);
        }
    }
    
    private void updateApiStatusUI(Boolean isConnected) {
        if (isConnected != null) {
            if (isConnected) {
                apiStatusText.setText("Connected");
                apiStatusDot.setBackgroundResource(R.drawable.gradient_circle_extended);
            } else {
                apiStatusText.setText("Disconnected");
                apiStatusDot.setBackgroundColor(getColor(R.color.error_red));
            }
        }
    }
    
    private void updateErrorUI(String error) {
        if (error != null && !error.isEmpty()) {
            errorMessage.setText(error);
            errorMessage.setVisibility(View.VISIBLE);
        } else {
            errorMessage.setVisibility(View.GONE);
        }
    }
    
    // Helper methods
    
    private int calculateEfficiencyPercentage(double energyLoad) {
        // Convert energy load to efficiency percentage (lower is better)
        if (energyLoad < 200) return 90; // Excellent
        if (energyLoad < 250) return 75; // Good
        if (energyLoad < 300) return 50; // Average
        if (energyLoad < 350) return 30; // Poor
        return 15; // Very poor
    }
    
    private String getEfficiencyText(int efficiency) {
        if (efficiency >= 80) return "Excellent Efficiency";
        if (efficiency >= 60) return "Good Efficiency";
        if (efficiency >= 40) return "Average Efficiency";
        if (efficiency >= 20) return "Poor Efficiency";
        return "Critical Usage";
    }
    
    private void updateGaugeColor(int efficiency) {
        int color;
        if (efficiency >= 60) {
            color = getColor(R.color.accent_green);
        } else if (efficiency >= 30) {
            color = getColor(R.color.warning_orange);
        } else {
            color = getColor(R.color.error_red);
        }
        usageGauge.setProgressTintList(android.content.res.ColorStateList.valueOf(color));
    }
    
    private String getOrdinalNumber(int number) {
        switch (number) {
            case 1: return "1st";
            case 2: return "2nd";
            case 3: return "3rd";
            default: return number + "th";
        }
    }
    
    // Modal functionality
    
    private void toggleModal() {
        if (modalOverlay.getVisibility() == View.VISIBLE) {
            hideModal();
        } else {
            showModal();
        }
    }
    
    private void showModal() {
        modalOverlay.setVisibility(View.VISIBLE);
        showModalTab("alerts"); // Default to alerts tab
    }
    
    private void hideModal() {
        modalOverlay.setVisibility(View.GONE);
    }
    
    private void showModalTab(String tabName) {
        // Reset all tab colors
        tabAlerts.setColorFilter(getColor(R.color.white));
        tabNotifications.setColorFilter(getColor(R.color.white));
        tabSettings.setColorFilter(getColor(R.color.white));
        tabProfile.setColorFilter(getColor(R.color.white));
        
        // Highlight selected tab and show content
        switch (tabName) {
            case "alerts":
                tabAlerts.setColorFilter(getColor(R.color.text_red));
                modalContentText.setText("ALERTS\n\nEnergy usage is currently optimal. Continue your great conservation efforts!");
                break;
            case "notifications":
                tabNotifications.setColorFilter(getColor(R.color.text_red));
                modalContentText.setText("NOTIFICATIONS\n\nLeaderboard updated! Your dorm ranking has improved. Check the latest energy savings tips.");
                break;
            case "settings":
                tabSettings.setColorFilter(getColor(R.color.text_red));
                modalContentText.setText("SETTINGS\n\nConfigure your energy monitoring preferences, notification settings, and dorm selection.");
                break;
            case "profile":
                tabProfile.setColorFilter(getColor(R.color.text_red));
                modalContentText.setText("PROFILE\n\nView your energy conservation achievements and personal statistics.");
                break;
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to the activity
        viewModel.forceRefresh();
    }
}
