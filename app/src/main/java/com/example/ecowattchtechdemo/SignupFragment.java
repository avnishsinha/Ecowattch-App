package com.example.ecowattchtechdemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

public class SignupFragment extends Fragment {
    Button signupButton;
    TextView loginLink;
    TextInputEditText signupUser, signupPass, confirmPass;
    AutoCompleteTextView dormDropdown;
    // theme manager
    ThemeManager tm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Link XML layout to this Fragment
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        signupButton = view.findViewById(R.id.signup_button);
        loginLink = view.findViewById(R.id.login_link);
        signupUser = view.findViewById(R.id.signup_user);
        signupPass = view.findViewById(R.id.signup_pass);
        confirmPass = view.findViewById(R.id.confirm_pass);
        dormDropdown = view.findViewById(R.id.dormitory);

        // Setup dormitory dropdown with dorm options
        String[] dormitories = {"Tinsley", "Gabaldon", "Sechrist"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                dormitories
        );
        dormDropdown.setAdapter(adapter);

        signupButton.setOnClickListener(v -> {
            String username = signupUser.getText().toString().trim();
            String password = signupPass.getText().toString().trim();
            String confirm = confirmPass.getText().toString().trim();
            String dormitory = dormDropdown.getText().toString().trim();

            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("usernames", username);
                jsonBody.put("passwords", password);
                jsonBody.put("dormitory", dormitory);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String url = "http://10.0.2.2:3000/signup";  // local API on emulator

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                    response -> {
                        Toast.makeText(requireContext(), "Sign-up successful!", Toast.LENGTH_SHORT).show();

                        //get the stuff from the sign up
                        SharedPreferences prefs = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("Username", username);
                        editor.putString("Dormitory", dormitory);
                        editor.apply();
                    },
                    error -> {
                        String errorMsg = "Sign-up failed";
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            try {
                                String responseBody = new String(error.networkResponse.data, "utf-8");
                                JSONObject data = new JSONObject(responseBody);
                                errorMsg = data.optString("message", errorMsg);
                            } catch (Exception e) {
                                // fallback to default errorMsg
                            }
                        }
                        Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show();
                        //Toast.makeText(getApplicationContext(), "Sign-up failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
            );

            Volley.newRequestQueue(requireContext()).add(request);

            // go to dashboard
            Intent intent = new Intent(requireContext(), DashboardActivity.class);
            startActivity(intent);
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tm = new ThemeManager(requireContext());
        tm.applyTheme();
    }
}
