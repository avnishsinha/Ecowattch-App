package com.example.ecowattchtechdemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ecowattchtechdemo.utils.Constants;

public class RecordsActivity extends AppCompatActivity {

    private TextView usernameText;
    private TextView energyPointsText;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        initViews();
        setupClickListeners();
        
        // Get user data from intent
        String username = getIntent().getStringExtra(Constants.EXTRA_USERNAME);
        String dormitory = getIntent().getStringExtra(Constants.EXTRA_DORMITORY);
        
        if (username != null) {
            usernameText.setText(username);
        }
        
        // Set energy points
        energyPointsText.setText(Constants.DEFAULT_ENERGY_POINTS);
    }

    private void initViews() {
        usernameText = findViewById(R.id.username_text);
        energyPointsText = findViewById(R.id.energy_points_text);
        backButton = findViewById(R.id.back_button);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, DashboardActivity.class);
            String username = getIntent().getStringExtra(Constants.EXTRA_USERNAME);
            String dormitory = getIntent().getStringExtra(Constants.EXTRA_DORMITORY);
            intent.putExtra(Constants.EXTRA_USERNAME, username);
            intent.putExtra(Constants.EXTRA_DORMITORY, dormitory);
            startActivity(intent);
            finish();
        });
    }
}
