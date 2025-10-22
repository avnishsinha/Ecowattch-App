package com.example.ecowattchtechdemo.willow.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

/**
 * Digital Twin model for Willow API responses
 */
public class DigitalTwin {
    
    @SerializedName("id")
    private String id;
    
    @SerializedName("modelId")
    private String modelId;
    
    @SerializedName("lastUpdateTime")
    private String lastUpdateTime;
    
    @SerializedName("contents")
    private Map<String, Object> contents;
    
    @SerializedName("incomingRelationships")
    private List<Relationship> incomingRelationships;
    
    @SerializedName("outgoingRelationships")
    private List<Relationship> outgoingRelationships;
    
    // Constructors
    public DigitalTwin() {}
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getModelId() {
        return modelId;
    }
    
    public void setModelId(String modelId) {
        this.modelId = modelId;
    }
    
    public String getLastUpdateTime() {
        return lastUpdateTime;
    }
    
    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
    
    public Map<String, Object> getContents() {
        return contents;
    }
    
    public void setContents(Map<String, Object> contents) {
        this.contents = contents;
    }
    
    public List<Relationship> getIncomingRelationships() {
        return incomingRelationships;
    }
    
    public void setIncomingRelationships(List<Relationship> incomingRelationships) {
        this.incomingRelationships = incomingRelationships;
    }
    
    public List<Relationship> getOutgoingRelationships() {
        return outgoingRelationships;
    }
    
    public void setOutgoingRelationships(List<Relationship> outgoingRelationships) {
        this.outgoingRelationships = outgoingRelationships;
    }
    
    /**
     * Get name from contents if available
     */
    public String getName() {
        if (contents != null && contents.containsKey("name")) {
            return contents.get("name").toString();
        }
        return id;
    }
    
    /**
     * Get a specific property from contents
     */
    public Object getProperty(String key) {
        if (contents != null) {
            return contents.get(key);
        }
        return null;
    }
    
    /**
     * Check if this twin is a building
     */
    public boolean isBuilding() {
        return modelId != null && modelId.contains("Building");
    }
    
    /**
     * Check if this twin is a capability (sensor/data point)
     */
    public boolean isCapability() {
        return modelId != null && modelId.contains("Capability");
    }
    
    @Override
    public String toString() {
        return "DigitalTwin{" +
                "id='" + id + '\'' +
                ", modelId='" + modelId + '\'' +
                ", name='" + getName() + '\'' +
                '}';
    }
}
