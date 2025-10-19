package com.example.ecowattchtechdemo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DashContentFragment extends Fragment {
    
    private TextView usernameText;
    private TextView currentUsageText;
    private TextView yesterdaysTotalText;
    private TextView potentialEnergyText;
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dash_content, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize all TextViews after view is created
        initializeViews(view);
    }
    
    private void initializeViews(View view) {
        usernameText = view.findViewById(R.id.username_text);
        currentUsageText = view.findViewById(R.id.current_usage_text);
        yesterdaysTotalText = view.findViewById(R.id.yesterdays_total_text);
        potentialEnergyText = view.findViewById(R.id.potential_energy_text);
        
        // Make the usage text clickable for manual refresh
        if (currentUsageText != null) {
            currentUsageText.setOnClickListener(v -> {
                if (getActivity() instanceof DashboardActivity) {
                    ((DashboardActivity) getActivity()).manualRefresh();
                }
            });
        }
        
        // Set up refresh button
        View refreshButton = view.findViewById(R.id.refresh_button);
        if (refreshButton != null) {
            refreshButton.setOnClickListener(v -> {
                if (getActivity() instanceof DashboardActivity) {
                    ((DashboardActivity) getActivity()).manualRefresh();
                }
            });
        }
    }
    
    /**
     * Update the current energy usage display
     */
    public void updateCurrentUsage(String usage) {
        if (currentUsageText != null) {
            currentUsageText.setText(usage);
        }
    }
    
    /**
     * Update the dorm status and leaderboard position
     */
    public void updateDormStatus(String dormInfo) {
        if (usernameText != null) {
            usernameText.setText(dormInfo);
        }
    }
    
    /**
     * Update yesterday's total energy usage
     */
    public void updateYesterdaysTotal(String total) {
        if (yesterdaysTotalText != null) {
            yesterdaysTotalText.setText(total);
        }
    }
    
    /**
     * Update the potential energy display
     */
    public void updatePotentialEnergy(String potentialEnergy) {
        if (potentialEnergyText != null) {
            potentialEnergyText.setText(potentialEnergy);
        }
    }
}