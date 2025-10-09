package com.example.ecowattchtechdemo.utils;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.ecowattchtechdemo.R;
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
    }
}
