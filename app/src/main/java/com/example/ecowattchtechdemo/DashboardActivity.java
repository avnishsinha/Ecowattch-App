package com.example.ecowattchtechdemo;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class DashboardActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private TextView usernameText;
    private TextView currentUsageText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initViews();
        setupViewPager();
        
        // Get username from intent
        String username = getIntent().getStringExtra("username");
        if (username != null) {
            usernameText.setText("Welcome, " + username);
        }
        
        // Simulate current usage
        currentUsageText.setText("240");
    }

    private void initViews() {
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        usernameText = findViewById(R.id.username_text);
        currentUsageText = findViewById(R.id.current_usage_text);
    }

    private void setupViewPager() {
        DashboardPagerAdapter adapter = new DashboardPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Dashboard");
                            break;
                        case 1:
                            tab.setText("Leaderboard");
                            break;
                        case 2:
                            tab.setText("Shop");
                            break;
                    }
                }
        ).attach();
    }

    private static class DashboardPagerAdapter extends FragmentStateAdapter {
        public DashboardPagerAdapter(FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new DashboardFragment();
                case 1:
                    return new LeaderboardFragment();
                case 2:
                    return new ShopFragment();
                default:
                    return new DashboardFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }
}
