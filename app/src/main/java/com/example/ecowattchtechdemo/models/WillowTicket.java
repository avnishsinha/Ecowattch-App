package com.example.ecowattchtechdemo.models;

import com.google.gson.annotations.SerializedName;

public class WillowTicket {
    @SerializedName("id")
    private String id;
    
    @SerializedName("summary")
    private String summary;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("priority")
    private String priority;
    
    @SerializedName("location_id")
    private String locationId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    @Override
    public String toString() {
        return "WillowTicket{" +
                "id='" + id + '\'' +
                ", summary='" + summary + '\'' +
                ", status='" + status + '\'' +
                ", priority='" + priority + '\'' +
                ", locationId='" + locationId + '\'' +
                '}';
    }
}
