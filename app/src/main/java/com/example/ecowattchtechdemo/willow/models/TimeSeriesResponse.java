package com.example.ecowattchtechdemo.willow.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Time series data response from Willow API
 */
public class TimeSeriesResponse {
    
    @SerializedName("data")
    private List<TimeSeriesPoint> data;
    
    @SerializedName("continuationToken")
    private String continuationToken;
    
    @SerializedName("errorData")
    private ErrorData errorData;
    
    // Constructors
    public TimeSeriesResponse() {}
    
    // Getters and Setters
    public List<TimeSeriesPoint> getData() {
        return data;
    }
    
    public void setData(List<TimeSeriesPoint> data) {
        this.data = data;
    }
    
    public String getContinuationToken() {
        return continuationToken;
    }
    
    public void setContinuationToken(String continuationToken) {
        this.continuationToken = continuationToken;
    }
    
    public ErrorData getErrorData() {
        return errorData;
    }
    
    public void setErrorData(ErrorData errorData) {
        this.errorData = errorData;
    }
    
    /**
     * Check if response has data
     */
    public boolean hasData() {
        return data != null && !data.isEmpty();
    }
    
    /**
     * Get latest value if available
     */
    public Double getLatestValue() {
        if (hasData()) {
            TimeSeriesPoint latest = data.get(data.size() - 1);
            return latest.getScalarValue();
        }
        return null;
    }
    
    @Override
    public String toString() {
        return "TimeSeriesResponse{" +
                "dataPoints=" + (data != null ? data.size() : 0) +
                ", hasError=" + (errorData != null) +
                '}';
    }
    
    /**
     * Error data class
     */
    public static class ErrorData {
        @SerializedName("message")
        private String message;
        
        @SerializedName("ids")
        private List<String> ids;
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
        
        public List<String> getIds() {
            return ids;
        }
        
        public void setIds(List<String> ids) {
            this.ids = ids;
        }
    }
}
