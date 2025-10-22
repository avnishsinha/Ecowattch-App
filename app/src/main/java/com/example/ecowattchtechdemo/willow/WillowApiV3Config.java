package com.example.ecowattchtechdemo.willow;

import com.example.ecowattchtechdemo.BuildConfig;

/**
 * Configuration class for Willow API v3 integration
 * Contains base URLs, endpoints, and configuration constants
 */
public class WillowApiV3Config {
    
    // Base URL - Loaded from BuildConfig (local.properties)
    public static final String DEFAULT_BASE_URL = BuildConfig.WILLOW_BASE_URL;
    
    // API Endpoints
    public static final String API_BASE_PATH = "/api/v3";
    public static final String OAUTH_TOKEN_ENDPOINT = "/oauth2/token";
    public static final String MODELS_ENDPOINT = "/models";
    public static final String TWINS_ENDPOINT = "/twins";
    public static final String TIME_SERIES_ENDPOINT = "/time-series";
    public static final String TIME_SERIES_LATEST_ENDPOINT = "/time-series/ids/latest";
    public static final String TWINS_SEARCH_ENDPOINT = "/twins";
    
    // OAuth2 Configuration
    public static final String GRANT_TYPE = "client_credentials";
    public static final String TOKEN_TYPE = "Bearer";
    
    // Building Twin IDs for NAU dormitories - Loaded from BuildConfig (local.properties)
    public static final String TWIN_ID_TINSLEY = BuildConfig.TWIN_ID_TINSLEY;
    public static final String TWIN_ID_GABALDON = BuildConfig.TWIN_ID_GABALDON; 
    public static final String TWIN_ID_SECHRIST = BuildConfig.TWIN_ID_SECHRIST;
    
    // Energy-related capability model IDs
    public static final String ENERGY_CONSUMPTION_MODEL = "dtmi:com:willowinc:EnergyConsumption;1";
    public static final String POWER_CONSUMPTION_MODEL = "dtmi:com:willowinc:PowerConsumption;1";
    public static final String ELECTRICAL_ENERGY_MODEL = "dtmi:com:willowinc:ElectricalEnergy;1";
    
    // Time series configuration
    public static final int DEFAULT_TIME_SERIES_LIMIT = 100;
    public static final long DEFAULT_TIME_RANGE_HOURS = 24;
    
    // Request timeouts (in milliseconds)
    public static final int CONNECTION_TIMEOUT = 30000;
    public static final int READ_TIMEOUT = 30000;
    public static final int WRITE_TIMEOUT = 30000;
    
    // Headers
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    
    /**
     * Get full API URL
     */
    public static String getApiUrl(String baseUrl) {
        return baseUrl + API_BASE_PATH;
    }
    
    /**
     * Get OAuth token URL
     */
    public static String getTokenUrl(String baseUrl) {
        return baseUrl + API_BASE_PATH + OAUTH_TOKEN_ENDPOINT;
    }
    
    /**
     * Get twins search URL
     */
    public static String getTwinsUrl(String baseUrl) {
        return baseUrl + API_BASE_PATH + TWINS_ENDPOINT;
    }
    
    /**
     * Get time series URL
     */
    public static String getTimeSeriesUrl(String baseUrl) {
        return baseUrl + API_BASE_PATH + TIME_SERIES_ENDPOINT;
    }
    
    /**
     * Get time series latest values URL  
     */
    public static String getTimeSeriesLatestUrl(String baseUrl) {
        return baseUrl + API_BASE_PATH + TIME_SERIES_LATEST_ENDPOINT;
    }
    
    /**
     * Get models URL
     */
    public static String getModelsUrl(String baseUrl) {
        return baseUrl + API_BASE_PATH + MODELS_ENDPOINT;
    }
}
