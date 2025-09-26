package com.example.ecowattchtechdemo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class LeaderboardFragment extends Fragment {

    private RecyclerView recyclerView;
    private LeaderboardAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);
        
        recyclerView = view.findViewById(R.id.leaderboard_recycler);
        setupRecyclerView();
        
        return view;
    }

    private void setupRecyclerView() {
        List<LeaderboardItem> items = new ArrayList<>();
        items.add(new LeaderboardItem("LoremIpsum25", "Tinsley", 100, 1));
        items.add(new LeaderboardItem("EcoWarrior", "Green Valley", 200, 2));
        items.add(new LeaderboardItem("PowerSaver", "Downtown", 240, 3));
        items.add(new LeaderboardItem("EnergyMaster", "Riverside", 180, 4));
        items.add(new LeaderboardItem("WattWise", "Hillside", 160, 5));

        adapter = new LeaderboardAdapter(items);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }
}
