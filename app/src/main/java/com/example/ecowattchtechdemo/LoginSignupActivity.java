package com.example.ecowattchtechdemo;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class LoginSignupActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_signup);

        // Only add the fragment if it’s not already added
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.login_signup_fragment_container, new LoginFragment())
                    .commit();
        }
    }
}
