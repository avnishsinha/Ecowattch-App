package com.example.ecowattchtechdemo.utils;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.view.View;
import android.content.Intent;

public class DashboardActivity extends AppCompatActivity {
    Button records, shop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        records = (android.widget.LinearLayout)findViewById(R.id.records_button);
        shop = (android.widget.LinearLayout)findViewById(R.id.shop_button);

        records.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, RecordsActivity.class);
                startActivity(intent);
            }
        });

        shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, ShopActivity.class);
                startActivity(intent);
            }
        });
    }

}
