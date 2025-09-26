package com.example.ecowattchtechdemo;

public class LeaderboardItem {
    private String username;
    private String location;
    private int points;
    private int rank;

    public LeaderboardItem(String username, String location, int points, int rank) {
        this.username = username;
        this.location = location;
        this.points = points;
        this.rank = rank;
    }

    // Getters
    public String getUsername() { return username; }
    public String getLocation() { return location; }
    public int getPoints() { return points; }
    public int getRank() { return rank; }
}
