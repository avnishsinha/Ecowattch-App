package com.example.ecowattchtechdemo.models;

import java.util.List;
import java.util.Map;

public class WillowTwinV3 {
    private String id;
    private String modelId;
    private String lastUpdateTime;
    private Map<String, Object> contents;
    private List<WillowRelationship> incomingRelationships;
    private List<WillowRelationship> outgoingRelationships;
    
    // Constructors
    public WillowTwinV3() {}
    
    public WillowTwinV3(String id, String modelId) {
        this.id = id;
        this.modelId = modelId;
    }
    
    // Getters
    public String getId() {
        return id;
    }
    
    public String getModelId() {
        return modelId;
    }
    
    public String getLastUpdateTime() {
        return lastUpdateTime;
    }
    
    public Map<String, Object> getContents() {
        return contents;
    }
    
    public List<WillowRelationship> getIncomingRelationships() {
        return incomingRelationships;
    }
    
    public List<WillowRelationship> getOutgoingRelationships() {
        return outgoingRelationships;
    }
    
    // Setters
    public void setId(String id) {
        this.id = id;
    }
    
    public void setModelId(String modelId) {
        this.modelId = modelId;
    }
    
    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
    
    public void setContents(Map<String, Object> contents) {
        this.contents = contents;
    }
    
    public void setIncomingRelationships(List<WillowRelationship> incomingRelationships) {
        this.incomingRelationships = incomingRelationships;
    }
    
    public void setOutgoingRelationships(List<WillowRelationship> outgoingRelationships) {
        this.outgoingRelationships = outgoingRelationships;
    }
    
    // Helper methods
    public String getName() {
        if (contents != null && contents.containsKey("name")) {
            return contents.get("name").toString();
        }
        return id; // Fallback to ID if name not available
    }
    
    public String getDisplayName() {
        // Try different possible name fields
        if (contents != null) {
            if (contents.containsKey("displayName")) {
                return contents.get("displayName").toString();
            }
            if (contents.containsKey("name")) {
                return contents.get("name").toString();
            }
            if (contents.containsKey("twinName")) {
                return contents.get("twinName").toString();
            }
        }
        return id;
    }
    
    public Object getProperty(String propertyName) {
        if (contents != null) {
            return contents.get(propertyName);
        }
        return null;
    }
    
    public boolean hasRelationships() {
        return (incomingRelationships != null && !incomingRelationships.isEmpty()) ||
               (outgoingRelationships != null && !outgoingRelationships.isEmpty());
    }
    
    public boolean isBuilding() {
        return modelId != null && modelId.contains("Building");
    }
    
    public boolean isRoom() {
        return modelId != null && modelId.contains("Room");
    }
    
    public boolean isAsset() {
        return modelId != null && modelId.contains("Asset");
    }
    
    public boolean isCapability() {
        return modelId != null && modelId.contains("Capability");
    }
    
    @Override
    public String toString() {
        return "WillowTwinV3{" +
                "id='" + id + '\'' +
                ", modelId='" + modelId + '\'' +
                ", name='" + getName() + '\'' +
                ", lastUpdateTime='" + lastUpdateTime + '\'' +
                ", hasContents=" + (contents != null) +
                ", incomingRels=" + (incomingRelationships != null ? incomingRelationships.size() : 0) +
                ", outgoingRels=" + (outgoingRelationships != null ? outgoingRelationships.size() : 0) +
                '}';
    }
    
    // Inner class for relationships
    public static class WillowRelationship {
        private String Id;
        private String targetId;
        private String sourceId;
        private String name;
        private Map<String, Object> properties;
        
        // Constructors
        public WillowRelationship() {}
        
        public WillowRelationship(String id, String sourceId, String targetId, String name) {
            this.Id = id;
            this.sourceId = sourceId;
            this.targetId = targetId;
            this.name = name;
        }
        
        // Getters
        public String getId() {
            return Id;
        }
        
        public String getTargetId() {
            return targetId;
        }
        
        public String getSourceId() {
            return sourceId;
        }
        
        public String getName() {
            return name;
        }
        
        public Map<String, Object> getProperties() {
            return properties;
        }
        
        // Setters
        public void setId(String id) {
            this.Id = id;
        }
        
        public void setTargetId(String targetId) {
            this.targetId = targetId;
        }
        
        public void setSourceId(String sourceId) {
            this.sourceId = sourceId;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public void setProperties(Map<String, Object> properties) {
            this.properties = properties;
        }
        
        @Override
        public String toString() {
            return "WillowRelationship{" +
                    "Id='" + Id + '\'' +
                    ", sourceId='" + sourceId + '\'' +
                    ", targetId='" + targetId + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
}
