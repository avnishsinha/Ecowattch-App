package com.example.ecowattchtechdemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.widget.RelativeLayout;
import android.view.ViewGroup;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

import com.example.ecowattchtechdemo.theme.ColorPalette;
import com.example.ecowattchtechdemo.theme.Palettes;
import com.example.ecowattchtechdemo.theme.ThemeManager;
import com.example.ecowattchtechdemo.theme.ThemeChangeListener;
import com.example.ecowattchtechdemo.theme.ThemeApplier;

public class ShopActivity extends AppCompatActivity implements ThemeChangeListener {
    private static final String TAG = "ShopActivity";
    Button backButton;
    TextView tabPallets, tabOwned, tabMore;
    RecyclerView palletsRecycler, ownedRecycler;

    TextView usernameText, dormitoryText;

    private ShopAdapter palletsAdapter;
    private ShopAdapter ownedAdapter;

    private List<ShopItem> palletsList;
    private List<ShopItem> ownedList;

    private ThemeManager themeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        // Initialize theme manager (singleton)
        themeManager = ThemeManager.getInstance(this);

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

        // Register as theme change listener and apply initial theme
        if (themeManager != null) {
            themeManager.addThemeChangeListener(this);
            onThemeChanged(themeManager.getCurrentPaletteName());
        }
    }

    private void initializeSampleData() {
        // Initialize with real palettes from theme system
        palletsList = new ArrayList<>();

        // Add all available palettes with gradient circle colors
        for (ColorPalette palette : Palettes.getAllPalettes()) {
            ShopItem item = new ShopItem(
                    palette.name,
                    400,  // TODO: BACKEND - Set actual prices per palette
                    R.drawable.gradient_circle_extended,
                    palette.name,
                    palette.accentColor,
                    palette.circleGradientLight,
                    palette.circleGradientDark
            );
            palletsList.add(item);
        }

        // Load owned palettes from theme preferences
        ownedList = new ArrayList<>();
        String currentPaletteId = themeManager.getCurrentPaletteName();

        // TODO: BACKEND - Load actual owned palettes from server
        // For now, all palettes are owned (but only PEACH by default)
        for (ColorPalette palette : Palettes.getAllPalettes()) {
            ShopItem ownedItem = new ShopItem(
                    palette.name,
                    400,
                    R.drawable.gradient_circle_extended,
                    palette.name,
                    palette.accentColor,
                    palette.circleGradientLight,
                    palette.circleGradientDark
            );
            ownedItem.setOwned(true);

            // Mark currently selected palette
            if (palette.name.equals(currentPaletteId)) {
                ownedItem.setSelected(true);
            }

            ownedList.add(ownedItem);
        }
    }

    private void setupRecyclerViews() {
        // Setup Pallets RecyclerView with horizontal layout
        LinearLayoutManager palletsLayoutManager = new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false);
        palletsRecycler.setLayoutManager(palletsLayoutManager);

        palletsAdapter = new ShopAdapter(palletsList, new ShopAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ShopItem item, int position) {
                // Handle palette purchase attempt
                // TODO: BACKEND - Implement actual purchase logic with energy cost
                if (item.getPaletteId() != null) {
                    // For now, allow selection of any palette (backend will handle purchase)
                    selectPalette(item.getPaletteId());
                }
            }
        }, this);
        palletsRecycler.setAdapter(palletsAdapter);

        // Setup Owned RecyclerView with horizontal layout
        LinearLayoutManager ownedLayoutManager = new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false);
        ownedRecycler.setLayoutManager(ownedLayoutManager);

        ownedAdapter = new ShopAdapter(ownedList, new ShopAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ShopItem item, int position) {
                // Handle owned palette selection - apply the theme immediately
                if (item.getPaletteId() != null) {
                    selectPalette(item.getPaletteId());
                }
            }
        }, this);
        ownedRecycler.setAdapter(ownedAdapter);
    }

    /**
     * Select and apply a palette theme
     * This method saves the selection and triggers live theme application
     */
    private void selectPalette(String paletteId) {
        themeManager.setPalette(paletteId);

        // Show confirmation toast
        Toast.makeText(this, paletteId + " palette selected! 🎨", Toast.LENGTH_SHORT).show();

        // Update owned list to show current selection
        updateOwnedListSelection();
        ownedAdapter.notifyDataSetChanged();
    }

    /**
     * Update owned list to mark the currently selected palette
     */
    private void updateOwnedListSelection() {
        String currentPalette = themeManager.getCurrentPaletteName();

        for (ShopItem item : ownedList) {
            if (item.getPaletteId() != null && item.getPaletteId().equals(currentPalette)) {
                item.setSelected(true);
            } else {
                item.setSelected(false);
            }
        }
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

    @Override
    public void onThemeChanged(String newPaletteName) {
        Log.d(TAG, "onThemeChanged called: " + newPaletteName);
        ThemeApplier.applyThemeToActivity(this, themeManager);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (themeManager != null) {
            themeManager.removeThemeChangeListener(this);
        }
    }
}
