package com.example.ecowattchtechdemo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ShopFragment extends Fragment {

    private RecyclerView recyclerView;
    private ShopAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop, container, false);
        
        recyclerView = view.findViewById(R.id.shop_recycler);
        setupRecyclerView();
        
        return view;
    }

    private void setupRecyclerView() {
        List<ShopItem> items = new ArrayList<>();
        items.add(new ShopItem("Smart LED Bulb", 50, "Energy-efficient LED bulb with app control"));
        items.add(new ShopItem("Power Strip Monitor", 120, "Track and control multiple devices"));
        items.add(new ShopItem("Smart Thermostat", 240, "AI-powered temperature control system"));
        items.add(new ShopItem("Solar Panel Kit", 500, "Generate your own clean energy"));
        items.add(new ShopItem("Energy Monitor", 80, "Real-time energy usage display"));
        items.add(new ShopItem("Smart Outlet", 30, "Control any device remotely"));

        adapter = new ShopAdapter(items, this::onItemClick);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(adapter);
    }

    private void onItemClick(ShopItem item) {
        // Handle item click - could show details or purchase dialog
    }
}
