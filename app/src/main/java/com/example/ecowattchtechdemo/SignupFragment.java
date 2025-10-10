package com.example.ecowattchtechdemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

public class SignupFragment extends Fragment {
    Button signupButton;
    TextView loginLink;
    TextInputEditText signupUser, signupPass, confirmPass, dorm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Link XML layout to this Fragment
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        signupButton = (Button)view.findViewById(R.id.signup_button);
        loginLink = (TextView)view.findViewById(R.id.login_link);
        signupUser = (TextInputEditText)view.findViewById(R.id.signup_user);
        signupPass = (TextInputEditText)view.findViewById(R.id.signup_pass);
        confirmPass = (TextInputEditText)view.findViewById(R.id.confirm_pass);
        dorm = (TextInputEditText)view.findViewById(R.id.dormitory);

        signupButton.setOnClickListener(v -> {
            String username = signupUser.getText().toString().trim();
            String password = signupPass.getText().toString().trim();
            String confirm = confirmPass.getText().toString().trim();
            String dormitory = dorm.getText().toString().trim();

            // handle signup logic
        });

        loginLink.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.login_signup_fragment_container, new LoginFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }
}
