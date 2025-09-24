package com.example.ecowattchtechdemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    private TextView usernameText;
    private TextView currentUsageText;
    private ImageView menuButton;
    private LinearLayout modalOverlay;
    private LinearLayout recordsButton;
    private LinearLayout shopButton;
    private boolean isModalVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initViews();
        setupClickListeners();
        
        // Get username from intent
        String username = getIntent().getStringExtra("username");
        if (username != null) {
            usernameText.setText(username.toUpperCase() + " -- 1ST PLACE");
        }
        
        // Simulate current usage
        currentUsageText.setText("280");
    }

    private void initViews() {
        usernameText = findViewById(R.id.username_text);
        currentUsageText = findViewById(R.id.current_usage_text);
        menuButton = findViewById(R.id.menu_button);
        modalOverlay = findViewById(R.id.modal_overlay);
        recordsButton = findViewById(R.id.records_button);
        shopButton = findViewById(R.id.shop_button);
    }

    private void setupClickListeners() {
        menuButton.setOnClickListener(v -> toggleModal());
        recordsButton.setOnClickListener(v -> {
            // TODO: Navigate to records page when implemented
            // Intent intent = new Intent(this, RecordsActivity.class);
            // startActivity(intent);
        });
        shopButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ShopActivity.class);
            String username = getIntent().getStringExtra("username");
            String dormitory = getIntent().getStringExtra("dormitory");
            intent.putExtra("username", username);
            intent.putExtra("dormitory", dormitory);
            startActivity(intent);
        });
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
}
