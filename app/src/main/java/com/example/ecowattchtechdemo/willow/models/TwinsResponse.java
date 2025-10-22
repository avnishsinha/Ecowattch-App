package com.example.ecowattchtechdemo.willow.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Twins search response wrapper
 */
public class TwinsResponse {
    
    @SerializedName("content")
    private List<DigitalTwin> content;
    
    @SerializedName("continuationToken")
    private String continuationToken;
    
    // Constructors
    public TwinsResponse() {}
    
    // Getters and Setters
    public List<DigitalTwin> getContent() {
        return content;
    }
    
    public void setContent(List<DigitalTwin> content) {
        this.content = content;
    }
    
    public String getContinuationToken() {
        return continuationToken;
    }
    
    public void setContinuationToken(String continuationToken) {
        this.continuationToken = continuationToken;
    }
    
    /**
     * Check if response has twins
     */
    public boolean hasTwins() {
        return content != null && !content.isEmpty();
    }
    
    @Override
    public String toString() {
        return "TwinsResponse{" +
                "twinCount=" + (content != null ? content.size() : 0) +
                '}';
    }
}
