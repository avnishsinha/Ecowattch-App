package com.example.ecowattchtechdemo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ViewHolder> {

    private List<ShopItem> items;

    public ShopAdapter(List<ShopItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shop, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShopItem item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView energyText;
        TextView nameText;
        ImageView colorSwatch;
        ImageView checkmark;

        ViewHolder(View itemView) {
            super(itemView);
            energyText = itemView.findViewById(R.id.energy_text);
            nameText = itemView.findViewById(R.id.name_text);
            colorSwatch = itemView.findViewById(R.id.color_swatch);
            checkmark = itemView.findViewById(R.id.checkmark);
        }

        void bind(ShopItem item) {
            energyText.setText(item.getPrice() + " Energy");
            nameText.setText(item.getName());
            
            // Set gradient background for color swatch
            colorSwatch.setBackgroundResource(R.drawable.gradient_circle);
            
            // Show/hide checkmark based on selection
            if (item.isSelected()) {
                checkmark.setVisibility(View.VISIBLE);
            } else {
                checkmark.setVisibility(View.GONE);
            }
        }
    }
}
