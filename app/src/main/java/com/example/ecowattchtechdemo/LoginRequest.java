package com.example.ecowattchtechdemo;

public class LoginRequest {
    private String usernames;
    private String passwords;

    public LoginRequest(String usernames, String passwords) {
        this.usernames = usernames;
        this.passwords = passwords;
    }
}
