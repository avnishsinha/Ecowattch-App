package com.example.ecowattchtechdemo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * RecyclerView adapter for displaying power consumption data points
 */
public class PowerDataAdapter extends RecyclerView.Adapter<PowerDataAdapter.PowerDataViewHolder> {
    
    private List<PowerDataDisplayActivity.PowerDataPoint> powerDataPoints;
    private DecimalFormat decimalFormat;
    private SimpleDateFormat timeFormat;
    
    public PowerDataAdapter(List<PowerDataDisplayActivity.PowerDataPoint> powerDataPoints) {
        this.powerDataPoints = powerDataPoints;
        this.decimalFormat = new DecimalFormat("#,##0.00");
        this.timeFormat = new SimpleDateFormat("MMM dd, HH:mm:ss", Locale.US);
    }
    
    @NonNull
    @Override
    public PowerDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_power_data, parent, false);
        return new PowerDataViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull PowerDataViewHolder holder, int position) {
        PowerDataDisplayActivity.PowerDataPoint dataPoint = powerDataPoints.get(position);
        
        try {
            // Format timestamp
            Date timestamp = new Date(parseTimestamp(dataPoint.timestamp));
            holder.timestampText.setText(timeFormat.format(timestamp));
            
            // Format power value
            holder.powerValueText.setText(decimalFormat.format(dataPoint.powerKw) + " kW");
            
            // Show raw value if different from formatted
            if (dataPoint.rawValue != null && !dataPoint.rawValue.trim().isEmpty()) {
                holder.rawValueText.setText("Raw: " + dataPoint.rawValue);
                holder.rawValueText.setVisibility(View.VISIBLE);
            } else {
                holder.rawValueText.setVisibility(View.GONE);
            }
            
            // Color coding based on power level
            int textColor;
            if (dataPoint.powerKw > 100) {
                textColor = holder.itemView.getContext().getColor(R.color.high_power_color);
            } else if (dataPoint.powerKw > 50) {
                textColor = holder.itemView.getContext().getColor(R.color.medium_power_color);
            } else {
                textColor = holder.itemView.getContext().getColor(R.color.low_power_color);
            }
            holder.powerValueText.setTextColor(textColor);
            
        } catch (Exception e) {
            // Fallback for any parsing errors
            holder.timestampText.setText("Invalid timestamp");
            holder.powerValueText.setText("Error");
            holder.rawValueText.setVisibility(View.GONE);
        }
    }
    
    @Override
    public int getItemCount() {
        return powerDataPoints.size();
    }
    
    private long parseTimestamp(String timestamp) {
        try {
            // Handle different timestamp formats
            if (timestamp.contains("T")) {
                // ISO format: 2024-01-15T14:30:00Z
                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
                return isoFormat.parse(timestamp).getTime();
            } else {
                // Fallback to current time if format is unexpected
                return System.currentTimeMillis();
            }
        } catch (Exception e) {
            return System.currentTimeMillis();
        }
    }
    
    public static class PowerDataViewHolder extends RecyclerView.ViewHolder {
        TextView timestampText;
        TextView powerValueText;
        TextView rawValueText;
        
        public PowerDataViewHolder(@NonNull View itemView) {
            super(itemView);
            timestampText = itemView.findViewById(R.id.timestamp_text);
            powerValueText = itemView.findViewById(R.id.power_value_text);
            rawValueText = itemView.findViewById(R.id.raw_value_text);
        }
    }
}
