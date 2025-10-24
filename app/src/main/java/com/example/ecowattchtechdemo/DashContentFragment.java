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
    // theme manager
    private ThemeManager tm;
    
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

        tm = new ThemeManager(requireContext());
        tm.applyTheme();
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
     * @param usage String in format "280kW" or just the number
     */
    public void updateCurrentUsage(String usage) {
        if (currentUsageText != null) {
            // Remove "kW" suffix if present, we display it separately
            String numericValue = usage.replace("kW", "").replace("kw", "").trim();
            currentUsageText.setText(numericValue);
        }
    }

    /**
     * Update the dorm status and leaderboard position
     * @param dormInfo String in format "TINSLEY - 1ST PLACE"
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
     * @param potentialEnergy String in format "237 Potential Energy" or just the number
     */
    public void updatePotentialEnergy(String potentialEnergy) {
        if (potentialEnergyText != null) {
            // Extract just the number if full string is provided
            String numericValue = potentialEnergy.replace(" Potential Energy", "").trim();
            potentialEnergyText.setText(numericValue);
        }
    }
}