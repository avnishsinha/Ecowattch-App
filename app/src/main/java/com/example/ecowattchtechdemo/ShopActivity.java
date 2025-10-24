package com.example.ecowattchtechdemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopActivity extends AppCompatActivity {
    Button backButton;
    TextView tabPallets, tabOwned, tabMore;
    RecyclerView palletsRecycler, ownedRecycler;

    TextView usernameText, dormitoryText;

    private ShopAdapter palletsAdapter;
    private ShopAdapter ownedAdapter;

    private List<ShopItem> palletsList;
    private List<ShopItem> ownedList;

    // theme manager
    private ThemeManager tm;

    // TEST PALETTE VALUES
    private final Map<String, String[]> paletteColors = new HashMap<String, String[]>() {{
        put("PEACH", new String[]{"#FFFFFF", "#AAAAAA", "#CD232E", "#1B1B1B", "#262626"});
        put("BLUE", new String[]{"#060606", "#1B1B1B", "#1956DB", "#BCBCBC", "#262626"});
        put("GREEN", new String[]{"#FFFFFF", "#AAAAAA", "#19BD53", "#38916A", "#262626"});
        put("MAGENTA", new String[]{"#0F0F0F", "#AAAAAA", "#D719DB", "#EFEFEF", "#262626"});
        put("CYAN", new String[]{"#FFFFFF", "#AAAAAA", "#19DBD1", "#313131", "#262626"});
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        // Initialize views
        backButton = findViewById(R.id.back_button);
        tabPallets = findViewById(R.id.tab_pallets);
        tabOwned = findViewById(R.id.tab_owned);
        tabMore = findViewById(R.id.tab_more);
        palletsRecycler = findViewById(R.id.pallets_recycler);
        ownedRecycler = findViewById(R.id.owned_recycler);

        usernameText = findViewById(R.id.username_text);
        dormitoryText = findViewById(R.id.dormitory_text);

        // load and display stored username/dormitory
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String username = prefs.getString("Username", "Valentino_Valero");
        String dorm = prefs.getString("Dormitory", "Tinsley");

        usernameText.setText(username);
        dormitoryText.setText(dorm);

        // Back button functionality
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Initialize sample data (BACKEND YOU REPLACE DATA HERE)
        initializeSampleData();

        // Setup RecyclerViews with horizontal scrolling
        setupRecyclerViews();

        // Setup tab click listeners
        setupTabs();

        // initialize ThemeManager
        tm = new ThemeManager(this);
    }

    protected void onStart() {
        super.onStart();
        tm.applyTheme();
    }

    protected void onResume() {
        super.onResume();
        tm.applyTheme();
    }

    private void initializeSampleData() {
        // Sample palette data - AND HERE
        palletsList = new ArrayList<>();
        palletsList.add(new ShopItem("PEACH", 400, R.drawable.gradient_circle_extended));
        palletsList.add(new ShopItem("BLUE", 500, R.drawable.gradient_circle_extended));
        palletsList.add(new ShopItem("GREEN", 600, R.drawable.gradient_circle_extended));
        palletsList.add(new ShopItem("MAGENTA", 450, R.drawable.gradient_circle_extended));
        palletsList.add(new ShopItem("CYAN", 550, R.drawable.gradient_circle_extended));
        palletsList.get(0).setOwned(true);
        palletsList.get(1).setOwned(true);

        // Sample owned items - backend will replace with user's owned items
        ownedList = new ArrayList<>();
        ownedList.add(new ShopItem("PEACH", 400, R.drawable.gradient_circle_extended));
        ownedList.get(0).setOwned(true);
        ownedList.add(new ShopItem("BLUE", 500, R.drawable.gradient_circle_extended));
        ownedList.get(1).setOwned(true);
    }

    private void setupRecyclerViews() {
        // Setup Pallets RecyclerView with horizontal layout
        LinearLayoutManager palletsLayoutManager = new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false);
        palletsRecycler.setLayoutManager(palletsLayoutManager);

        palletsAdapter = new ShopAdapter(palletsList, new ShopAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ShopItem item, int position) {
                // Handle item click - backend can add purchase logic here - Risa you use palette handler here)

                // get colors for clicked palette
                // TEMP/TODO: get colors[] from backend
                String[] colors = paletteColors.get(item.getName());
                if (colors == null || colors.length < 5) return;

                // check for owned - only select if palette is owned
                if (item.isOwned()) {
                    // get color values from backend, store in SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("ThemePrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();

                    editor.putString("primary_color", colors[0]);
                    editor.putString("secondary_color", colors[1]);
                    editor.putString("accent_color", colors[2]);
                    editor.putString("background_main", colors[3]);
                    editor.putString("background_light", colors[4]);

                    editor.apply();

                    // apply theme
                    onResume();
                } else {
                    previewPalette(colors);
                }
            }
        });
        palletsRecycler.setAdapter(palletsAdapter);

        // Setup Owned RecyclerView with horizontal layout
        LinearLayoutManager ownedLayoutManager = new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false);
        ownedRecycler.setLayoutManager(ownedLayoutManager);

        ownedAdapter = new ShopAdapter(ownedList, new ShopAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ShopItem item, int position) {
                // Handle owned item click - MORE BACKEND HERE
            }
        });
        ownedRecycler.setAdapter(ownedAdapter);
    }

    private void previewPalette(String[] colors) {
        // temporary color map
        Map<String, Integer> previewColors = new HashMap<>();
        previewColors.put("primary_color", Color.parseColor(colors[0]));
        previewColors.put("secondary_color", Color.parseColor(colors[1]));
        previewColors.put("accent_color", Color.parseColor(colors[2]));
        previewColors.put("primary_text", Color.parseColor(colors[0]));
        previewColors.put("secondary_text", Color.parseColor(colors[1]));
        previewColors.put("accent_text", Color.parseColor(colors[2]));
        previewColors.put("background_main", Color.parseColor(colors[3]));
        previewColors.put("background_light", Color.parseColor(colors[4]));

        // apply preview colors
        ThemeManager themeManager = new ThemeManager(this);
        themeManager.applyTheme(previewColors);
    }

    private void setupTabs() {
        // Pallets tab click listener
        tabPallets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToTab(tabPallets);
                palletsRecycler.setVisibility(View.VISIBLE);
                ownedRecycler.setVisibility(View.GONE);
            }
        });

        // Owned tab click listener
        tabOwned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToTab(tabOwned);
                palletsRecycler.setVisibility(View.GONE);
                ownedRecycler.setVisibility(View.VISIBLE);
            }
        });

        // More tab click listener (placeholder for alpha)
        tabMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Placeholder for future tabs like icons
            }
        });

        // Set initial tab state
        switchToTab(tabPallets);
    }

    private void switchToTab(TextView selectedTab) {
        // Reset all tabs to normal style
        tabPallets.setTypeface(null, Typeface.NORMAL);
        tabOwned.setTypeface(null, Typeface.NORMAL);
        tabMore.setTypeface(null, Typeface.NORMAL);

        // Set selected tab to bold
        selectedTab.setTypeface(null, Typeface.BOLD);
    }
}
