package com.example.ecowattchtechdemo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class SignupActivity extends AppCompatActivity {

    Button signupButton;
    TextInputEditText signupUser, signupPass, confirmPass, dormitory;
    TextView loginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        signupButton = findViewById(R.id.signup_button);
        signupUser = findViewById(R.id.signup_user);
        signupPass = findViewById(R.id.signup_pass);
        confirmPass = findViewById(R.id.confirm_pass);
        dormitory = findViewById(R.id.dormitory);
        loginLink = findViewById(R.id.login_link);
    }

    private void setupClickListeners() {
        signupButton.setOnClickListener(v -> handleSignup());
        loginLink.setOnClickListener(v -> navigateToLogin());
    }

    private void handleSignup() {
        String username = signupUser.getText().toString().trim();
        String password = signupPass.getText().toString().trim();
        String confirmPassword = confirmPass.getText().toString().trim();
        String dorm = dormitory.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || dorm.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
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

        // Navigate to dashboard
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("dormitory", dorm);
        startActivity(intent);
        finish();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
