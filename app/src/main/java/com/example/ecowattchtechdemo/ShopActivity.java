package com.example.ecowattchtechdemo;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Shop Activity - Energy points shop for purchasing rewards
 */
public class ShopActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        
        // Set up basic UI
        TextView titleText = findViewById(R.id.shop_title);
        titleText.setText("Energy Points Shop");
        
        TextView contentText = findViewById(R.id.shop_content);
        contentText.setText("Spend your energy points on:\n\n" +
                "• Campus dining credits\n" +
                "• Study room reservations\n" +
                "• Eco-friendly merchandise\n" +
                "• Priority parking passes\n" +
                "• Dorm amenity upgrades\n\n" +
                "Points are awarded at the end of each rally based on your dorm's performance!");
        
        // Back button
        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());
    }
}
