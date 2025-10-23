package com.example.ecowattchtechdemo;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    private String status;
    private String message;

    @SerializedName("user")
    private User user;


    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public User getUser() { return user; }

}
