package com.example.ecowattchtechdemo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ShopActivity extends AppCompatActivity {

    private TextView usernameText;
    private TextView dormitoryText;
    private TextView energyPointsText;
    private Button backButton;
    private RecyclerView shopRecycler;
    private ShopAdapter shopAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        initViews();
        setupClickListeners();
        setupRecyclerView();
        
        // Get user data from intent
        String username = getIntent().getStringExtra("username");
        String dormitory = getIntent().getStringExtra("dormitory");
        
        if (username != null) {
            usernameText.setText(username);
        }
        if (dormitory != null) {
            dormitoryText.setText(dormitory);
        }
        
        // Set energy points
        energyPointsText.setText("400 Energy");
    }

    private void initViews() {
        usernameText = findViewById(R.id.username_text);
        dormitoryText = findViewById(R.id.dormitory_text);
        energyPointsText = findViewById(R.id.energy_points_text);
        backButton = findViewById(R.id.back_button);
        shopRecycler = findViewById(R.id.shop_recycler);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, DashboardActivity.class);
            String username = getIntent().getStringExtra("username");
            String dormitory = getIntent().getStringExtra("dormitory");
            intent.putExtra("username", username);
            intent.putExtra("dormitory", dormitory);
            startActivity(intent);
            finish();
        });
    }

    private void setupRecyclerView() {
        List<ShopItem> shopItems = new ArrayList<>();
        
        // Add sample shop items
        shopItems.add(new ShopItem("PEACH", 400, true));
        shopItems.add(new ShopItem("OCEAN", 400, false));
        shopItems.add(new ShopItem("FOREST", 400, false));
        shopItems.add(new ShopItem("SUNSET", 400, false));
        
        shopAdapter = new ShopAdapter(shopItems);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        shopRecycler.setLayoutManager(layoutManager);
        shopRecycler.setAdapter(shopAdapter);
    }
}
