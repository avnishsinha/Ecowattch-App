package com.example.ecowattchtechdemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.ecowattchtechdemo.utils.Constants;
import com.example.ecowattchtechdemo.utils.StyleUtils;

public class DashboardActivity extends AppCompatActivity {

    private TextView usernameText;
    private TextView currentUsageText;
    private ImageView menuButton;
    private LinearLayout modalOverlay;
    private LinearLayout recordsButton;
    private LinearLayout shopButton;
    private boolean isModalVisible = false;
    
    // Modal tab views
    private ImageView tabAlerts, tabNotifications, tabSettings, tabProfile;
    private LinearLayout tabContentAlerts, tabContentNotifications, tabContentSettings, tabContentProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initViews();
        setupClickListeners();
        
        // Get username from intent
        String username = getIntent().getStringExtra(Constants.EXTRA_USERNAME);
        if (username != null) {
            setupStyledUsernameText(username.toUpperCase());
        }
        
        // Simulate current usage with styled text
        setupStyledUsageText();
    }

    private void initViews() {
        usernameText = findViewById(R.id.username_text);
        currentUsageText = findViewById(R.id.current_usage_text);
        menuButton = findViewById(R.id.menu_button);
        modalOverlay = findViewById(R.id.modal_overlay);
        recordsButton = findViewById(R.id.records_button);
        shopButton = findViewById(R.id.shop_button);
        
        // Initialize modal tab views
        tabAlerts = findViewById(R.id.tab_alerts);
        tabNotifications = findViewById(R.id.tab_notifications);
        tabSettings = findViewById(R.id.tab_settings);
        tabProfile = findViewById(R.id.tab_profile);
        
        tabContentAlerts = findViewById(R.id.tab_content_alerts);
        tabContentNotifications = findViewById(R.id.tab_content_notifications);
        tabContentSettings = findViewById(R.id.tab_content_settings);
        tabContentProfile = findViewById(R.id.tab_content_profile);
    }

    private void setupClickListeners() {
        menuButton.setOnClickListener(v -> toggleModal());
        recordsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, RecordsActivity.class);
            String username = getIntent().getStringExtra(Constants.EXTRA_USERNAME);
            String dormitory = getIntent().getStringExtra(Constants.EXTRA_DORMITORY);
            intent.putExtra(Constants.EXTRA_USERNAME, username);
            intent.putExtra(Constants.EXTRA_DORMITORY, dormitory);
            startActivity(intent);
        });
        shopButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ShopActivity.class);
            String username = getIntent().getStringExtra(Constants.EXTRA_USERNAME);
            String dormitory = getIntent().getStringExtra(Constants.EXTRA_DORMITORY);
            intent.putExtra(Constants.EXTRA_USERNAME, username);
            intent.putExtra(Constants.EXTRA_DORMITORY, dormitory);
            startActivity(intent);
        });
        
        // Setup modal tab click listeners
        tabAlerts.setOnClickListener(v -> switchTab(Constants.TAB_ALERTS));
        tabNotifications.setOnClickListener(v -> switchTab(Constants.TAB_NOTIFICATIONS));
        tabSettings.setOnClickListener(v -> switchTab(Constants.TAB_SETTINGS));
        tabProfile.setOnClickListener(v -> switchTab(Constants.TAB_PROFILE));
    }

    private void toggleModal() {
        if (isModalVisible) {
            modalOverlay.setVisibility(View.GONE);
            isModalVisible = false;
        } else {
            modalOverlay.setVisibility(View.VISIBLE);
            isModalVisible = true;
        }
    }

    private void setupStyledUsernameText(String username) {
        usernameText.setText(StyleUtils.createStyledUsernameText(this, username));
    }

    private void setupStyledUsageText() {
        currentUsageText.setText(StyleUtils.createStyledUsageText(this, Constants.DEFAULT_USAGE));
    }

    private void switchTab(int tabIndex) {
        // Hide all content
        tabContentAlerts.setVisibility(View.GONE);
        tabContentNotifications.setVisibility(View.GONE);
        tabContentSettings.setVisibility(View.GONE);
        tabContentProfile.setVisibility(View.GONE);
        
        // Reset all tab tints to white
        tabAlerts.setColorFilter(ContextCompat.getColor(this, R.color.white));
        tabNotifications.setColorFilter(ContextCompat.getColor(this, R.color.white));
        tabSettings.setColorFilter(ContextCompat.getColor(this, R.color.white));
        tabProfile.setColorFilter(ContextCompat.getColor(this, R.color.white));
        
        // Show selected content and highlight tab
        switch (tabIndex) {
            case Constants.TAB_ALERTS:
                tabContentAlerts.setVisibility(View.VISIBLE);
                tabAlerts.setColorFilter(ContextCompat.getColor(this, R.color.text_red));
                break;
            case Constants.TAB_NOTIFICATIONS:
                tabContentNotifications.setVisibility(View.VISIBLE);
                tabNotifications.setColorFilter(ContextCompat.getColor(this, R.color.text_red));
                break;
            case Constants.TAB_SETTINGS:
                tabContentSettings.setVisibility(View.VISIBLE);
                tabSettings.setColorFilter(ContextCompat.getColor(this, R.color.text_red));
                break;
            case Constants.TAB_PROFILE:
                tabContentProfile.setVisibility(View.VISIBLE);
                tabProfile.setColorFilter(ContextCompat.getColor(this, R.color.text_red));
                break;
        }
    }
}
