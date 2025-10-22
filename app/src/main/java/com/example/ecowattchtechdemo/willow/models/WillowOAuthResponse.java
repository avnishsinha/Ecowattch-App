package com.example.ecowattchtechdemo.willow.models;

import com.google.gson.annotations.SerializedName;

/**
 * OAuth2 Token Response from Willow API
 */
public class WillowOAuthResponse {
    
    @SerializedName("access_token")
    private String accessToken;
    
    @SerializedName("token_type") 
    private String tokenType;
    
    @SerializedName("expires_in")
    private int expiresIn;
    
    @SerializedName("ext_expires_in")
    private int extExpiresIn;
    
    // Constructors
    public WillowOAuthResponse() {}
    
    public WillowOAuthResponse(String accessToken, String tokenType, int expiresIn) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
    }
    
    // Getters and Setters
    public String getAccessToken() {
        return accessToken;
    }
    
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    
    public String getTokenType() {
        return tokenType;
    }
    
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
    
    public int getExpiresIn() {
        return expiresIn;
    }
    
    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }
    
    public int getExtExpiresIn() {
        return extExpiresIn;
    }
    
    public void setExtExpiresIn(int extExpiresIn) {
        this.extExpiresIn = extExpiresIn;
    }
    
    /**
     * Get full authorization header value
     */
    public String getAuthorizationHeader() {
        return tokenType + " " + accessToken;
    }
    
    /**
     * Check if token is valid (has access token)
     */
    public boolean isValid() {
        return accessToken != null && !accessToken.isEmpty();
    }
    
    @Override
    public String toString() {
        return "WillowOAuthResponse{" +
                "tokenType='" + tokenType + '\'' +
                ", expiresIn=" + expiresIn +
                ", hasToken=" + (accessToken != null && !accessToken.isEmpty()) +
                '}';
    }
}
