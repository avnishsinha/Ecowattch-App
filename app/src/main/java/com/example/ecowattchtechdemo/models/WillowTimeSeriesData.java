package com.example.ecowattchtechdemo.models;

import java.util.Map;

public class WillowTimeSeriesData {
    private String connectorId;
    private String twinId;
    private String externalId;
    private String trendId;
    private String sourceTimestamp;
    private String enqueuedTimestamp;
    private double scalarValue;
    private Map<String, Object> properties;
    private DataQuality dataQuality;
    
    // Constructors
    public WillowTimeSeriesData() {}
    
    public WillowTimeSeriesData(String twinId, String sourceTimestamp, double scalarValue) {
        this.twinId = twinId;
        this.sourceTimestamp = sourceTimestamp;
        this.scalarValue = scalarValue;
    }
    
    // Getters
    public String getConnectorId() {
        return connectorId;
    }
    
    public String getTwinId() {
        return twinId;
    }
    
    public String getExternalId() {
        return externalId;
    }
    
    public String getTrendId() {
        return trendId;
    }
    
    public String getSourceTimestamp() {
        return sourceTimestamp;
    }
    
    public String getEnqueuedTimestamp() {
        return enqueuedTimestamp;
    }
    
    public double getScalarValue() {
        return scalarValue;
    }
    
    public Map<String, Object> getProperties() {
        return properties;
    }
    
    public DataQuality getDataQuality() {
        return dataQuality;
    }
    
    // Setters
    public void setConnectorId(String connectorId) {
        this.connectorId = connectorId;
    }
    
    public void setTwinId(String twinId) {
        this.twinId = twinId;
    }
    
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }
    
    public void setTrendId(String trendId) {
        this.trendId = trendId;
    }
    
    public void setSourceTimestamp(String sourceTimestamp) {
        this.sourceTimestamp = sourceTimestamp;
    }
    
    public void setEnqueuedTimestamp(String enqueuedTimestamp) {
        this.enqueuedTimestamp = enqueuedTimestamp;
    }
    
    public void setScalarValue(double scalarValue) {
        this.scalarValue = scalarValue;
    }
    
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
    
    public void setDataQuality(DataQuality dataQuality) {
        this.dataQuality = dataQuality;
    }
    
    // Helper methods
    public boolean hasGoodQuality() {
        return dataQuality == null || dataQuality.isGoodQuality();
    }
    
    public String getFormattedValue() {
        return String.format("%.2f", scalarValue);
    }
    
    @Override
    public String toString() {
        return "WillowTimeSeriesData{" +
                "twinId='" + twinId + '\'' +
                ", sourceTimestamp='" + sourceTimestamp + '\'' +
                ", scalarValue=" + scalarValue +
                ", hasGoodQuality=" + hasGoodQuality() +
                '}';
    }
    
    // Inner class for data quality information
    public static class DataQuality {
        private boolean offline;
        private boolean valueOutOfRange;
        private boolean sparse;
        private boolean flatline;
        private boolean delayed;
        
        // Constructors
        public DataQuality() {}
        
        public DataQuality(boolean offline, boolean valueOutOfRange, boolean sparse, boolean flatline, boolean delayed) {
            this.offline = offline;
            this.valueOutOfRange = valueOutOfRange;
            this.sparse = sparse;
            this.flatline = flatline;
            this.delayed = delayed;
        }
        
        // Getters
        public boolean isOffline() {
            return offline;
        }
        
        public boolean isValueOutOfRange() {
            return valueOutOfRange;
        }
        
        public boolean isSparse() {
            return sparse;
        }
        
        public boolean isFlatline() {
            return flatline;
        }
        
        public boolean isDelayed() {
            return delayed;
        }
        
        // Setters
        public void setOffline(boolean offline) {
            this.offline = offline;
        }
        
        public void setValueOutOfRange(boolean valueOutOfRange) {
            this.valueOutOfRange = valueOutOfRange;
        }
        
        public void setSparse(boolean sparse) {
            this.sparse = sparse;
        }
        
        public void setFlatline(boolean flatline) {
            this.flatline = flatline;
        }
        
        public void setDelayed(boolean delayed) {
            this.delayed = delayed;
        }
        
        // Helper methods
        public boolean isGoodQuality() {
            return !offline && !valueOutOfRange && !sparse && !flatline && !delayed;
        }
        
        public String getQualityIssues() {
            StringBuilder issues = new StringBuilder();
            if (offline) issues.append("offline ");
            if (valueOutOfRange) issues.append("out-of-range ");
            if (sparse) issues.append("sparse ");
            if (flatline) issues.append("flatline ");
            if (delayed) issues.append("delayed ");
            
            return issues.length() > 0 ? issues.toString().trim() : "good";
        }
        
        @Override
        public String toString() {
            return "DataQuality{" +
                    "quality='" + getQualityIssues() + '\'' +
                    '}';
        }
    }
}
