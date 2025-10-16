package com.example.ecowattchtechdemo;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Records Activity - Shows energy usage history and rally progress
 */
public class RecordsActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);
        
        // Set up basic UI
        TextView titleText = findViewById(R.id.records_title);
        titleText.setText("Energy Records & Rally Progress");
        
        TextView contentText = findViewById(R.id.records_content);
        contentText.setText("Here you can view:\n\n" +
                "• Daily energy usage history\n" +
                "• Weekly consumption trends\n" +
                "• Rally participation statistics\n" +
                "• Energy conservation achievements\n" +
                "• Comparison with other dorms");
        
        // Back button
        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());
    }
}
