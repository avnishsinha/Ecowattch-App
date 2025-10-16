package com.example.ecowattchtechdemo.config;

public class WillowApiV3Config {
    // Base Willow API URL - V3 with organization-specific endpoint
    // Format: https://<organization>.app.willowinc.com/api/v3
    public static final String DEFAULT_BASE_URL = "https://northernarizonauniversity.app.willowinc.com/";
    public static final String API_VERSION = "v3";
    
    // OAuth2 Authentication Configuration
    public static final String OAUTH2_ENDPOINT = "/oauth2/token";
    public static final String GRANT_TYPE = "client_credentials";
    
    // V3 API Endpoints
    public static final String ENDPOINT_OAUTH2_TOKEN = "/oauth2/token";
    public static final String ENDPOINT_MODELS = "/models";
    public static final String ENDPOINT_TWINS = "/twins";
    public static final String ENDPOINT_TWINS_IDS = "/twins/ids";
    public static final String ENDPOINT_TWINS_GRAPH = "/twins/graph";
    public static final String ENDPOINT_TWINS_COUNT = "/twins/count";
    public static final String ENDPOINT_RELATIONSHIPS = "/twins/{twinId}/relationships";
    public static final String ENDPOINT_TIME_SERIES = "/time-series";
    public static final String ENDPOINT_TIME_SERIES_IDS = "/time-series/ids";
    public static final String ENDPOINT_TIME_SERIES_LATEST = "/time-series/{twinId}/latest";
    public static final String ENDPOINT_TIME_SERIES_IDS_LATEST = "/time-series/ids/latest";
    public static final String ENDPOINT_INSIGHTS = "/insights";
    public static final String ENDPOINT_EVENTS = "/events";
    public static final String ENDPOINT_TICKETS = "/tickets";
    
    // Authentication parameters
    public static final String PARAM_CLIENT_ID = "client_id";
    public static final String PARAM_CLIENT_SECRET = "client_secret";
    public static final String PARAM_GRANT_TYPE = "grant_type";
    
    // Common request parameters
    public static final String PARAM_PAGE_SIZE = "pageSize";
    public static final String PARAM_CONTINUATION_TOKEN = "continuationToken";
    public static final String PARAM_START = "start";
    public static final String PARAM_END = "end";
    public static final String PARAM_INCLUDE_DATA_QUALITY = "includeDataQuality";
    public static final String PARAM_INCLUDE_RELATIONSHIPS = "includeRelationships";
    
    // Model types (DTMI format)
    public static final String MODEL_BUILDING = "dtmi:com:willowinc:Building;1";
    public static final String MODEL_ROOM = "dtmi:com:willowinc:Room;1";
    public static final String MODEL_LEVEL = "dtmi:com:willowinc:Level;1";
    public static final String MODEL_ASSET = "dtmi:com:willowinc:Asset;1";
    public static final String MODEL_CAPABILITY = "dtmi:com:willowinc:Capability;1";
    
    // Known Twin IDs (if available - these may need to be discovered)
    public static final String TINSLEY_TWIN_ID = "PNT9CnuLmTV4tkZwAigypXrnY";  // May need to be updated for v3
    public static final String GABALDON_TWIN_ID = "PNT6hrRL8shRqbwLaXsbhDrBC";  // May need to be updated for v3
    
    // Target buildings for testing
    public static final String[] TARGET_BUILDINGS = {"Tinsley", "Gabaldon"};
    
    // HTTP timeouts (in seconds)
    public static final int CONNECT_TIMEOUT = 30;
    public static final int READ_TIMEOUT = 30;
    public static final int WRITE_TIMEOUT = 30;
    
    // HTTP headers
    public static final String AUTH_HEADER = "Authorization";
    public static final String AUTH_BEARER_PREFIX = "Bearer ";
    public static final String CONTENT_TYPE_HEADER = "Content-Type";
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";
    
    // OAuth2 Token Response Fields
    public static final String TOKEN_TYPE = "token_type";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String EXPIRES_IN = "expires_in";
    public static final String EXT_EXPIRES_IN = "ext_expires_in";
    
    // Success messages
    public static final String SUCCESS_AUTH = "✓ Authentication successful";
    public static final String SUCCESS_TWIN_LOOKUP = "✓ Twin found";
    public static final String SUCCESS_TWINS_RETRIEVED = "✓ Twins retrieved";
    public static final String SUCCESS_TIME_SERIES_RETRIEVED = "✓ Time series data retrieved";
    public static final String SUCCESS_INSIGHTS_RETRIEVED = "✓ Insights retrieved";
    public static final String SUCCESS_TICKETS_RETRIEVED = "✓ Tickets retrieved";
    
    // Error messages
    public static final String ERROR_NETWORK = "❌ Network error";
    public static final String ERROR_API = "❌ API error";
    public static final String ERROR_PARSING = "❌ Parsing error";
    public static final String ERROR_AUTH = "❌ Authentication failed";
    public static final String ERROR_TWIN_NOT_FOUND = "❌ Twin not found";
    public static final String ERROR_NO_DATA = "⚠️ No data available";
    public static final String ERROR_INVALID_CREDENTIALS = "❌ Invalid client credentials";
    public static final String ERROR_TOKEN_EXPIRED = "❌ Access token expired";
    
    // Log prefixes
    public static final String LOG_AUTH = "Auth: ";
    public static final String LOG_STEP_1 = "Step 1: ";
    public static final String LOG_STEP_2 = "Step 2: ";
    public static final String LOG_STEP_3 = "Step 3: ";
    
    // Helper method to get twin ID for a building name (legacy support)
    public static String getTwinIdForBuilding(String buildingName) {
        switch (buildingName.toLowerCase()) {
            case "tinsley":
                return TINSLEY_TWIN_ID;
            case "gabaldon":
                return GABALDON_TWIN_ID;
            default:
                return null;
        }
    }
    
    // Helper method to check if a building is supported
    public static boolean isBuildingSupported(String buildingName) {
        return getTwinIdForBuilding(buildingName) != null;
    }
    
    // Helper method to format organization-specific base URL
    public static String formatBaseUrl(String organization) {
        if (organization == null || organization.trim().isEmpty()) {
            return DEFAULT_BASE_URL;
        }
        // Remove any protocol and trailing slash if provided
        String cleanOrg = organization.trim()
                .replace("https://", "")
                .replace("http://", "")
                .replaceAll("/$", "");
        
        // If it's already a full URL, use it as is but ensure correct API path
        if (cleanOrg.contains(".app.willowinc.com")) {
            return "https://" + cleanOrg + (cleanOrg.endsWith("/api/v3") ? "" : "/api/v3");
        }
        
        // Otherwise, build the URL from organization name
        return String.format("https://%s.app.willowinc.com/api/v3", cleanOrg);
    }
    
    // Helper method to validate organization name
    public static boolean isValidOrganization(String organization) {
        return organization != null && 
               !organization.trim().isEmpty() && 
               organization.matches("^[a-zA-Z0-9-]+$");
    }
    
    // Helper method to get building details for logging and display
    public static String getBuildingDetails(String buildingName) {
        if (buildingName == null) {
            return "Unknown building";
        }
        
        String twinId = getTwinIdForBuilding(buildingName);
        if (twinId != null) {
            return String.format("%s Hall (Twin ID: %s)", buildingName, twinId);
        } else {
            return String.format("%s (Not configured)", buildingName);
        }
    }
    
    // Helper method to build model filter for API requests
    public static String buildModelFilter(String... modelIds) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < modelIds.length; i++) {
            sb.append("\"").append(modelIds[i]).append("\"");
            if (i < modelIds.length - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }
    
    // Default pagination settings
    public static final int DEFAULT_PAGE_SIZE = 100;
    public static final int MAX_PAGE_SIZE = 1000;
    
    // Time series specific constants
    public static final String TIME_SERIES_INCLUDE_DATA_QUALITY = "includeDataQuality";
    public static final long DEFAULT_TIME_RANGE_HOURS = 24; // 24 hours default
    
    // Filter operators for twin queries (based on V3 API documentation)
    public static final String FILTER_EQUALS = "=";
    public static final String FILTER_IN = "in";
    public static final String FILTER_CONTAINS = "contains";
    public static final String FILTER_STARTS_WITH = "startsWith";
    public static final String FILTER_AND = "And";
    public static final String FILTER_OR = "Or";
    
    // Relationship directions
    public static final String RELATIONSHIP_INCOMING = "Incoming";
    public static final String RELATIONSHIP_OUTGOING = "Outgoing";
    
    // Common relationship names
    public static final String RELATIONSHIP_IS_PART_OF = "isPartOf";
    public static final String RELATIONSHIP_IS_CAPABILITY_OF = "isCapabilityOf";
    public static final String RELATIONSHIP_LOCATED_IN = "locatedIn";
    public static final String RELATIONSHIP_SERVED_BY = "servedBy";
    public static final String RELATIONSHIP_HOSTED_BY = "hostedBy";
}
