package com.example.ecowattchtechdemo;

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
        return inflater.inflate(R.layout.fragment_signup, container, false);

        signupButton = (Button)container.findViewById(R.id.signup_button);
        loginLink = (TextView)container.findViewById(R.id.login_link);
        signupUser = (TextInputEditText)container.findViewById(R.id.signup_user);
        signupPass = (TextInputEditText)container.findViewById(R.id.signup_pass);
        confirmPass = (TextInputEditText)container.findViewById(R.id.confirm_pass);
        dorm = (TextInputEditText)container.findViewById(R.id.dormitory);
    }
}
