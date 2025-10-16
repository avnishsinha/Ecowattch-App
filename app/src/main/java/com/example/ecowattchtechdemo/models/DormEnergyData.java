package com.example.ecowattchtechdemo.models;

/**
 * Model representing a dorm/building with energy data and leaderboard information
 */
public class DormEnergyData {
    private String dormName;
    private int currentRank;
    private double currentEnergyLoad; // in kW
    private double yesterdayTotal; // in kWh
    private int potentialEnergyPoints;
    private String twinId; // Willow API twin ID
    private long lastUpdated;
    private boolean isUserDorm;
    
    public DormEnergyData() {
        this.lastUpdated = System.currentTimeMillis();
    }
    
    public DormEnergyData(String dormName, String twinId) {
        this.dormName = dormName;
        this.twinId = twinId;
        this.lastUpdated = System.currentTimeMillis();
    }
    
    // Getters and setters
    public String getDormName() {
        return dormName;
    }
    
    public void setDormName(String dormName) {
        this.dormName = dormName;
    }
    
    public int getCurrentRank() {
        return currentRank;
    }
    
    public void setCurrentRank(int currentRank) {
        this.currentRank = currentRank;
    }
    
    public double getCurrentEnergyLoad() {
        return currentEnergyLoad;
    }
    
    public void setCurrentEnergyLoad(double currentEnergyLoad) {
        this.currentEnergyLoad = currentEnergyLoad;
    }
    
    public double getYesterdayTotal() {
        return yesterdayTotal;
    }
    
    public void setYesterdayTotal(double yesterdayTotal) {
        this.yesterdayTotal = yesterdayTotal;
    }
    
    public int getPotentialEnergyPoints() {
        return potentialEnergyPoints;
    }
    
    public void setPotentialEnergyPoints(int potentialEnergyPoints) {
        this.potentialEnergyPoints = potentialEnergyPoints;
    }
    
    public String getTwinId() {
        return twinId;
    }
    
    public void setTwinId(String twinId) {
        this.twinId = twinId;
    }
    
    public long getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    public boolean isUserDorm() {
        return isUserDorm;
    }
    
    public void setUserDorm(boolean userDorm) {
        isUserDorm = userDorm;
    }
    
    // Helper methods
    public String getFormattedRank() {
        switch (currentRank) {
            case 1:
                return "1ST PLACE";
            case 2:
                return "2ND PLACE";
            case 3:
                return "3RD PLACE";
            default:
                return currentRank + "TH PLACE";
        }
    }
    
    public String getFormattedEnergyLoad() {
        return String.format("%.0fkW", currentEnergyLoad);
    }
    
    public String getFormattedYesterdayTotal() {
        return String.format("Yesterday's Total: %.0fkWh", yesterdayTotal);
    }
    
    public String getFormattedPotentialEnergy() {
        return potentialEnergyPoints + " Potential Energy";
    }
    
    public boolean isDataStale() {
        // Data is stale if it's older than 30 minutes
        return (System.currentTimeMillis() - lastUpdated) > (30 * 60 * 1000);
    }
    
    @Override
    public String toString() {
        return "DormEnergyData{" +
                "dormName='" + dormName + '\'' +
                ", currentRank=" + currentRank +
                ", currentEnergyLoad=" + currentEnergyLoad +
                ", twinId='" + twinId + '\'' +
                '}';
    }
}
