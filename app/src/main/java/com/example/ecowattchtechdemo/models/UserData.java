package com.example.ecowattchtechdemo.models;

/**
 * Model representing user data including their energy points and preferences
 */
public class UserData {
    private String username;
    private String selectedDorm;
    private int spendableEnergyPoints;
    private int currentRallyDaysLeft;
    private boolean notificationsEnabled;
    private long lastLogin;
    
    public UserData() {
        this.lastLogin = System.currentTimeMillis();
        this.notificationsEnabled = true;
    }
    
    public UserData(String username, String selectedDorm) {
        this.username = username;
        this.selectedDorm = selectedDorm;
        this.lastLogin = System.currentTimeMillis();
        this.notificationsEnabled = true;
    }
    
    // Getters and setters
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getSelectedDorm() {
        return selectedDorm;
    }
    
    public void setSelectedDorm(String selectedDorm) {
        this.selectedDorm = selectedDorm;
    }
    
    public int getSpendableEnergyPoints() {
        return spendableEnergyPoints;
    }
    
    public void setSpendableEnergyPoints(int spendableEnergyPoints) {
        this.spendableEnergyPoints = spendableEnergyPoints;
    }
    
    public int getCurrentRallyDaysLeft() {
        return currentRallyDaysLeft;
    }
    
    public void setCurrentRallyDaysLeft(int currentRallyDaysLeft) {
        this.currentRallyDaysLeft = currentRallyDaysLeft;
    }
    
    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }
    
    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }
    
    public long getLastLogin() {
        return lastLogin;
    }
    
    public void setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    // Helper methods
    public String getFormattedEnergyPoints() {
        return spendableEnergyPoints + " energy";
    }
    
    public String getFormattedDaysLeft() {
        if (currentRallyDaysLeft <= 0) {
            return "Rally Ended!";
        } else if (currentRallyDaysLeft == 1) {
            return "1 Day Left!";
        } else {
            return currentRallyDaysLeft + " Days Left!";
        }
    }
    
    @Override
    public String toString() {
        return "UserData{" +
                "username='" + username + '\'' +
                ", selectedDorm='" + selectedDorm + '\'' +
                ", spendableEnergyPoints=" + spendableEnergyPoints +
                '}';
    }
}
