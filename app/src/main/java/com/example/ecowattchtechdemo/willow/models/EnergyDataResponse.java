package com.example.ecowattchtechdemo.willow.models;

/**
 * Energy data wrapper for dashboard consumption
 */
public class EnergyDataResponse {
    
    private String buildingName;
    private String buildingId;
    private Double currentUsageKW;
    private Double dailyTotalKWh;
    private Double potentialEnergy;
    private String lastUpdated;
    private boolean dataAvailable;
    private String status;
    
    // Constructors
    public EnergyDataResponse() {}
    
    public EnergyDataResponse(String buildingName, String buildingId) {
        this.buildingName = buildingName;
        this.buildingId = buildingId;
        this.dataAvailable = false;
        this.status = "No Data";
    }
    
    // Getters and Setters
    public String getBuildingName() {
        return buildingName;
    }
    
    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }
    
    public String getBuildingId() {
        return buildingId;
    }
    
    public void setBuildingId(String buildingId) {
        this.buildingId = buildingId;
    }
    
    public Double getCurrentUsageKW() {
        return currentUsageKW;
    }
    
    public void setCurrentUsageKW(Double currentUsageKW) {
        this.currentUsageKW = currentUsageKW;
    }
    
    public Double getDailyTotalKWh() {
        return dailyTotalKWh;
    }
    
    public void setDailyTotalKWh(Double dailyTotalKWh) {
        this.dailyTotalKWh = dailyTotalKWh;
    }
    
    public Double getPotentialEnergy() {
        return potentialEnergy;
    }
    
    public void setPotentialEnergy(Double potentialEnergy) {
        this.potentialEnergy = potentialEnergy;
    }
    
    public String getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    public boolean isDataAvailable() {
        return dataAvailable;
    }
    
    public void setDataAvailable(boolean dataAvailable) {
        this.dataAvailable = dataAvailable;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    /**
     * Get current usage as integer for display
     */
    public int getCurrentUsageAsInt() {
        if (currentUsageKW != null) {
            return currentUsageKW.intValue();
        }
        return 0;
    }
    
    /**
     * Get daily total as integer for display
     */
    public int getDailyTotalAsInt() {
        if (dailyTotalKWh != null) {
            return dailyTotalKWh.intValue();
        }
        return 0;
    }
    
    /**
     * Calculate potential energy based on efficiency
     */
    public void calculatePotentialEnergy() {
        if (currentUsageKW != null) {
            // Simple calculation: potential energy = max efficiency - current usage
            this.potentialEnergy = Math.max(0, 300.0 - (currentUsageKW - 200.0));
        }
    }
    
    @Override
    public String toString() {
        return "EnergyDataResponse{" +
                "buildingName='" + buildingName + '\'' +
                ", currentUsageKW=" + currentUsageKW +
                ", dailyTotalKWh=" + dailyTotalKWh +
                ", dataAvailable=" + dataAvailable +
                ", status='" + status + '\'' +
                '}';
    }
}
