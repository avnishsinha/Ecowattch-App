package com.example.ecowattchtechdemo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ecowattchtechdemo.theme.ThemeManager;

public class DashContentFragment extends Fragment {

    private TextView usernameText;
    private TextView currentUsageText;
    private TextView yesterdaysTotalText;
    private TextView potentialEnergyText;
    private TextView usageUnitText;
    private TextView potentialEnergyLabelText;
    private TextView refreshButton;
    private LinearLayout potentialEnergyPill;
    private ThemeManager themeManager;
    
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
        // Apply theme colors immediately
        applyThemeColors();
    }
    
    private void initializeViews(View view) {
        usernameText = view.findViewById(R.id.username_text);
        currentUsageText = view.findViewById(R.id.current_usage_text);
        yesterdaysTotalText = view.findViewById(R.id.yesterdays_total_text);
        potentialEnergyText = view.findViewById(R.id.potential_energy_text);
        usageUnitText = view.findViewById(R.id.usage_unit_text);
        potentialEnergyLabelText = view.findViewById(R.id.potential_energy_label);
        refreshButton = view.findViewById(R.id.refresh_button);

        // Initialize theme manager (singleton)
        if (getActivity() != null) {
            themeManager = ThemeManager.getInstance(getActivity());
        }

        // Make the usage text clickable for manual refresh
        if (currentUsageText != null) {
            currentUsageText.setOnClickListener(v -> {
                if (getActivity() instanceof DashboardActivity) {
                    ((DashboardActivity) getActivity()).manualRefresh();
                }
            });
        }

        // Set up refresh button
        if (refreshButton != null) {
            refreshButton.setOnClickListener(v -> {
                if (getActivity() instanceof DashboardActivity) {
                    ((DashboardActivity) getActivity()).manualRefresh();
                }
            });
        }
    }

    /**
     * Apply current theme colors to all UI elements
     */
    public void applyThemeColors() {
        if (themeManager == null || getActivity() == null) {
            android.util.Log.d("DashContentFragment", "applyThemeColors skipped: themeManager=" + (themeManager == null) + ", activity=" + (getActivity() == null));
            return;
        }

        int accentColor = themeManager.getAccentColor();
        android.util.Log.d("DashContentFragment", "applyThemeColors: accentColor=0x" + Integer.toHexString(accentColor));

        // Apply accent color to dorm name (e.g., "TINSLEY")
        if (usernameText != null) {
            usernameText.setTextColor(accentColor);
            android.util.Log.d("DashContentFragment", "Updated usernameText color");
        }

        // Apply accent color to the "kw" unit text
        if (usageUnitText != null) {
            usageUnitText.setTextColor(accentColor);
            android.util.Log.d("DashContentFragment", "Updated usageUnitText color");
        }

        // Apply accent color to potential energy number
        if (potentialEnergyText != null) {
            potentialEnergyText.setTextColor(accentColor);
            android.util.Log.d("DashContentFragment", "Updated potentialEnergyText color");
        }

        // Apply accent color to refresh button
        if (refreshButton != null) {
            refreshButton.setTextColor(accentColor);
            android.util.Log.d("DashContentFragment", "Updated refreshButton color");
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