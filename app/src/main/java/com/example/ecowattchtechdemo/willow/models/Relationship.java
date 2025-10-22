package com.example.ecowattchtechdemo.willow.models;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

/**
 * Relationship model for digital twins
 */
public class Relationship {
    
    @SerializedName("Id")
    private String id;
    
    @SerializedName("targetId")
    private String targetId;
    
    @SerializedName("sourceId")
    private String sourceId;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("properties")
    private Map<String, Object> properties;
    
    // Constructors
    public Relationship() {}
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getTargetId() {
        return targetId;
    }
    
    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }
    
    public String getSourceId() {
        return sourceId;
    }
    
    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Map<String, Object> getProperties() {
        return properties;
    }
    
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
    
    @Override
    public String toString() {
        return "Relationship{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", sourceId='" + sourceId + '\'' +
                ", targetId='" + targetId + '\'' +
                '}';
    }
}
