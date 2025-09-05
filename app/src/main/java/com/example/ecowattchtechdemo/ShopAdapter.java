package com.example.ecowattchtechdemo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ViewHolder> {

    private List<ShopItem> items;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ShopItem item);
    }

    public ShopAdapter(List<ShopItem> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
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
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        TextView priceText;
        TextView descriptionText;

        ViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.item_name);
            priceText = itemView.findViewById(R.id.item_price);
            descriptionText = itemView.findViewById(R.id.item_description);
        }

        void bind(ShopItem item, OnItemClickListener listener) {
            nameText.setText(item.getName());
            priceText.setText(item.getPrice() + " points");
            descriptionText.setText(item.getDescription());
            
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
