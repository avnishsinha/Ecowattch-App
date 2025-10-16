package com.example.ecowattchtechdemo.models;

public class OAuth2TokenResponse {
    private String token_type;
    private int expires_in;
    private int ext_expires_in;
    private String access_token;
    
    // Constructors
    public OAuth2TokenResponse() {}
    
    public OAuth2TokenResponse(String token_type, int expires_in, int ext_expires_in, String access_token) {
        this.token_type = token_type;
        this.expires_in = expires_in;
        this.ext_expires_in = ext_expires_in;
        this.access_token = access_token;
    }
    
    // Getters
    public String getTokenType() {
        return token_type;
    }
    
    public int getExpiresIn() {
        return expires_in;
    }
    
    public int getExtExpiresIn() {
        return ext_expires_in;
    }
    
    public String getAccessToken() {
        return access_token;
    }
    
    // Setters
    public void setTokenType(String token_type) {
        this.token_type = token_type;
    }
    
    public void setExpiresIn(int expires_in) {
        this.expires_in = expires_in;
    }
    
    public void setExtExpiresIn(int ext_expires_in) {
        this.ext_expires_in = ext_expires_in;
    }
    
    public void setAccessToken(String access_token) {
        this.access_token = access_token;
    }
    
    // Helper methods
    public boolean isValid() {
        return access_token != null && !access_token.isEmpty() && 
               token_type != null && token_type.equalsIgnoreCase("Bearer");
    }
    
    public long getExpirationTimeMillis() {
        return System.currentTimeMillis() + (expires_in * 1000L);
    }
    
    @Override
    public String toString() {
        return "OAuth2TokenResponse{" +
                "token_type='" + token_type + '\'' +
                ", expires_in=" + expires_in +
                ", ext_expires_in=" + ext_expires_in +
                ", access_token='" + (access_token != null ? access_token.substring(0, Math.min(10, access_token.length())) + "..." : "null") + '\'' +
                '}';
    }
}
