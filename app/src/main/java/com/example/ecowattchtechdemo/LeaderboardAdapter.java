package com.example.ecowattchtechdemo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {

    private List<LeaderboardItem> items;

    public LeaderboardAdapter(List<LeaderboardItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_leaderboard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LeaderboardItem item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText;
        TextView locationText;
        TextView pointsText;
        TextView rankText;

        ViewHolder(View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.username_text);
            locationText = itemView.findViewById(R.id.location_text);
            pointsText = itemView.findViewById(R.id.points_text);
            rankText = itemView.findViewById(R.id.rank_text);
        }

        void bind(LeaderboardItem item) {
            usernameText.setText(item.getUsername());
            locationText.setText(item.getLocation());
            pointsText.setText(item.getPoints() + " points");
            rankText.setText("#" + item.getRank());
        }
    }
}
