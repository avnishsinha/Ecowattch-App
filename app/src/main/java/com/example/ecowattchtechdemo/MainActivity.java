package com.example.ecowattchtechdemo;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    Button loginButton, signupButton;
    TextInputEditText loginUser, loginPass, signupUser, signupPass;
    
    // Notification constants
    public static final String CHANNEL_ID = "i.apps.notifications";
    public static final int NOTIFICATION_ID = 1234;
    public static final String DESCRIPTION = "Test notification";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupClickListeners();
        createNotificationChannel();
    }

    private void initViews() {
        loginButton = findViewById(R.id.login_button);
        signupButton = findViewById(R.id.signup_button);
        loginUser = findViewById(R.id.login_user);
        loginPass = findViewById(R.id.login_pass);
        signupUser = findViewById(R.id.signup_user);
        signupPass = findViewById(R.id.signup_pass);
    }

    private void setupClickListeners() {
        loginButton.setOnClickListener(v -> handleLogin());
        signupButton.setOnClickListener(v -> handleSignup());
    }

    private void handleLogin() {
        String username = loginUser.getText().toString().trim();
        String password = loginPass.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Simple validation - in real app, you'd validate against a database
        if (username.length() >= 3 && password.length() >= 6) {
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
            navigateToDashboard(username);
        } else {
            Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleSignup() {
        String username = signupUser.getText().toString().trim();
        String password = signupPass.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (username.length() < 3) {
            Toast.makeText(this, "Username must be at least 3 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        // Request notification permission and send notification
        requestNotificationPermissionAndSend();
        
        // Clear signup fields
        signupUser.setText("");
        signupPass.setText("");
        
        Toast.makeText(this, "Account created successfully! You can now log in.", Toast.LENGTH_LONG).show();
    }

    private void navigateToDashboard(String username) {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
        finish(); // Close login activity
    }

    private void requestNotificationPermissionAndSend() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]
                        {android.Manifest.permission.POST_NOTIFICATIONS}, 101);
                return;
            }
        }
        sendNotification();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_ID,
                    DESCRIPTION,
                    NotificationManager.IMPORTANCE_HIGH
            );

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.enableVibration(true);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private void sendNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("EcoWattch Account Created")
                .setContentText("Successfully created your account. You may now log in.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) 
                == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, builder.build());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendNotification();
            }
        }
    }
}