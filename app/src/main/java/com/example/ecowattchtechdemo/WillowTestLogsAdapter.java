package com.example.ecowattchtechdemo;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class WillowTestLogsAdapter extends RecyclerView.Adapter<WillowTestLogsAdapter.LogViewHolder> {
    
    private List<String> logMessages;

    public WillowTestLogsAdapter(List<String> logMessages) {
        this.logMessages = logMessages;
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        String message = logMessages.get(position);
        holder.textView.setText(message);
        
        // Color code different types of messages
        if (message.startsWith("✓") || message.startsWith("✅")) {
            holder.textView.setTextColor(Color.parseColor("#4CAF50")); // Green
        } else if (message.startsWith("❌") || message.startsWith("⚠️")) {
            holder.textView.setTextColor(Color.parseColor("#F44336")); // Red
        } else if (message.startsWith("🚀") || message.startsWith("🏢")) {
            holder.textView.setTextColor(Color.parseColor("#2196F3")); // Blue
        } else if (message.startsWith("Step")) {
            holder.textView.setTextColor(Color.parseColor("#FF9800")); // Orange
        } else {
            holder.textView.setTextColor(Color.parseColor("#757575")); // Gray
        }
        
        // Set smaller text size for logs
        holder.textView.setTextSize(12);
    }

    @Override
    public int getItemCount() {
        return logMessages.size();
    }

    static class LogViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        LogViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}
