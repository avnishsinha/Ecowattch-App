package com.example.ecowattchtechdemo.willow.models;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

/**
 * Individual time series data point
 */
public class TimeSeriesPoint {
    
    @SerializedName("connectorId")
    private String connectorId;
    
    @SerializedName("twinId")
    private String twinId;
    
    @SerializedName("externalId")
    private String externalId;
    
    @SerializedName("trendId")
    private String trendId;
    
    @SerializedName("sourceTimestamp")
    private String sourceTimestamp;
    
    @SerializedName("enqueuedTimestamp")
    private String enqueuedTimestamp;
    
    @SerializedName("scalarValue")
    private Double scalarValue;
    
    @SerializedName("properties")
    private Map<String, Object> properties;
    
    @SerializedName("dataQuality")
    private DataQuality dataQuality;
    
    // Constructors
    public TimeSeriesPoint() {}
    
    // Getters and Setters
    public String getConnectorId() {
        return connectorId;
    }
    
    public void setConnectorId(String connectorId) {
        this.connectorId = connectorId;
    }
    
    public String getTwinId() {
        return twinId;
    }
    
    public void setTwinId(String twinId) {
        this.twinId = twinId;
    }
    
    public String getExternalId() {
        return externalId;
    }
    
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }
    
    public String getTrendId() {
        return trendId;
    }
    
    public void setTrendId(String trendId) {
        this.trendId = trendId;
    }
    
    public String getSourceTimestamp() {
        return sourceTimestamp;
    }
    
    public void setSourceTimestamp(String sourceTimestamp) {
        this.sourceTimestamp = sourceTimestamp;
    }
    
    public String getEnqueuedTimestamp() {
        return enqueuedTimestamp;
    }
    
    public void setEnqueuedTimestamp(String enqueuedTimestamp) {
        this.enqueuedTimestamp = enqueuedTimestamp;
    }
    
    public Double getScalarValue() {
        return scalarValue;
    }
    
    public void setScalarValue(Double scalarValue) {
        this.scalarValue = scalarValue;
    }
    
    public Map<String, Object> getProperties() {
        return properties;
    }
    
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
    
    public DataQuality getDataQuality() {
        return dataQuality;
    }
    
    public void setDataQuality(DataQuality dataQuality) {
        this.dataQuality = dataQuality;
    }
    
    /**
     * Get value as kilowatts (assuming base unit conversion if needed)
     */
    public Double getValueAsKW() {
        if (scalarValue == null) return null;
        
        // Convert based on properties or assume it's already in kW
        return scalarValue;
    }
    
    @Override
    public String toString() {
        return "TimeSeriesPoint{" +
                "twinId='" + twinId + '\'' +
                ", scalarValue=" + scalarValue +
                ", sourceTimestamp='" + sourceTimestamp + '\'' +
                '}';
    }
    
    /**
     * Data quality indicators
     */
    public static class DataQuality {
        @SerializedName("offline")
        private boolean offline;
        
        @SerializedName("valueOutOfRange")
        private boolean valueOutOfRange;
        
        @SerializedName("sparse")
        private boolean sparse;
        
        @SerializedName("flatline")
        private boolean flatline;
        
        @SerializedName("delayed")
        private boolean delayed;
        
        // Getters and Setters
        public boolean isOffline() {
            return offline;
        }
        
        public void setOffline(boolean offline) {
            this.offline = offline;
        }
        
        public boolean isValueOutOfRange() {
            return valueOutOfRange;
        }
        
        public void setValueOutOfRange(boolean valueOutOfRange) {
            this.valueOutOfRange = valueOutOfRange;
        }
        
        public boolean isSparse() {
            return sparse;
        }
        
        public void setSparse(boolean sparse) {
            this.sparse = sparse;
        }
        
        public boolean isFlatline() {
            return flatline;
        }
        
        public void setFlatline(boolean flatline) {
            this.flatline = flatline;
        }
        
        public boolean isDelayed() {
            return delayed;
        }
        
        public void setDelayed(boolean delayed) {
            this.delayed = delayed;
        }
        
        /**
         * Check if data quality is good
         */
        public boolean isGoodQuality() {
            return !offline && !valueOutOfRange && !flatline;
        }
    }
}
