package com.example.ecowattchtechdemo.api;

import android.util.Log;
import com.example.ecowattchtechdemo.config.WillowApiV3Config;
import com.example.ecowattchtechdemo.models.OAuth2TokenResponse;
import com.example.ecowattchtechdemo.models.WillowTwinV3;
import com.example.ecowattchtechdemo.models.WillowTimeSeriesData;
import com.example.ecowattchtechdemo.models.WillowInsight;
import com.example.ecowattchtechdemo.models.WillowTicket;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Willow API v3 Service with OAuth2 Authentication
 * 
 * This service implements the new Willow API v3 which uses:
 * - OAuth2 Client Credentials Grant for authentication
 * - Organization-specific endpoints
 * - Enhanced twin querying with filters
 * - New time series API structure
 */
public class WillowApiV3Service {
    private static final String TAG = "WillowApiV3Service";
    
    private final OkHttpClient httpClient;
    private final Gson gson;
    
    // Configuration
    private String organization;
    private String baseUrl;
    
    // OAuth2 Credentials
    private String clientId;
    private String clientSecret;
    
    // Authentication state
    private OAuth2TokenResponse currentToken;
    private long tokenExpirationTime;

    public WillowApiV3Service() {
        // Create HTTP client with logging
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        
        httpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(WillowApiV3Config.CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(WillowApiV3Config.READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WillowApiV3Config.WRITE_TIMEOUT, TimeUnit.SECONDS)
                .build();
        
        gson = new Gson();
        baseUrl = WillowApiV3Config.DEFAULT_BASE_URL;
    }
    
    // Configuration methods
    public void setOrganization(String organization) {
        if (organization != null && !organization.trim().isEmpty()) {
            this.organization = organization.trim();
            this.baseUrl = WillowApiV3Config.formatBaseUrl(organization);
            Log.d(TAG, "Base URL set to: " + this.baseUrl);
        } else {
            Log.w(TAG, "Invalid organization input: " + organization);
        }
    }
    
    public void setCredentials(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }
    
    public void setBaseUrl(String url) {
        this.baseUrl = url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }
    
    public String getBaseUrl() {
        return baseUrl;
    }

    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }

    // OAuth2 Authentication
    public void authenticate(ApiCallback<OAuth2TokenResponse> callback) {
        if (clientId == null || clientSecret == null) {
            callback.onError(WillowApiV3Config.ERROR_INVALID_CREDENTIALS + ": Client ID and Secret required");
            return;
        }
        
        // Create form data for OAuth2 token request
        RequestBody formBody = new FormBody.Builder()
                .add(WillowApiV3Config.PARAM_CLIENT_ID, clientId)
                .add(WillowApiV3Config.PARAM_CLIENT_SECRET, clientSecret)
                .add(WillowApiV3Config.PARAM_GRANT_TYPE, WillowApiV3Config.GRANT_TYPE)
                .build();
        
        String url = baseUrl + WillowApiV3Config.ENDPOINT_OAUTH2_TOKEN;
        
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .addHeader(WillowApiV3Config.CONTENT_TYPE_HEADER, WillowApiV3Config.CONTENT_TYPE_FORM)
                .build();

        Log.d(TAG, "Authenticating with OAuth2 endpoint: " + url);
        
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "OAuth2 authentication failed", e);
                callback.onError(WillowApiV3Config.ERROR_NETWORK + ": " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseBody = response.body().string();
                    Log.d(TAG, "OAuth2 response code: " + response.code());
                    
                    if (response.isSuccessful()) {
                        OAuth2TokenResponse tokenResponse = gson.fromJson(responseBody, OAuth2TokenResponse.class);
                        
                        if (tokenResponse.isValid()) {
                            currentToken = tokenResponse;
                            tokenExpirationTime = tokenResponse.getExpirationTimeMillis();
                            Log.d(TAG, "OAuth2 authentication successful, token expires in " + tokenResponse.getExpiresIn() + " seconds");
                            callback.onSuccess(tokenResponse);
                        } else {
                            callback.onError(WillowApiV3Config.ERROR_AUTH + ": Invalid token response");
                        }
                    } else {
                        Log.e(TAG, "OAuth2 error response: " + responseBody);
                        callback.onError(WillowApiV3Config.ERROR_API + ": " + response.code() + " " + response.message());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing OAuth2 response", e);
                    callback.onError(WillowApiV3Config.ERROR_PARSING + ": " + e.getMessage());
                } finally {
                    response.close();
                }
            }
        });
    }
    
    // Check if we have a valid access token
    private boolean hasValidToken() {
        return currentToken != null && 
               currentToken.isValid() && 
               System.currentTimeMillis() < tokenExpirationTime - 60000; // 1 minute buffer
    }
    
    // Execute with authentication - ensures we have a valid token before proceeding
    private void executeWithAuth(AuthenticatedAction action, ApiCallback<?> callback) {
        if (!hasValidToken()) {
            // Need to authenticate first
            authenticate(new ApiCallback<OAuth2TokenResponse>() {
                @Override
                public void onSuccess(OAuth2TokenResponse token) {
                    // Execute the action with the new token
                    action.execute(token);
                }
                
                @Override
                public void onError(String error) {
                    callback.onError(error);
                }
            });
        } else {
            // Use existing token
            action.execute(currentToken);
        }
    }
    
    // Interface for authenticated actions
    public interface AuthenticatedAction {
        void execute(OAuth2TokenResponse token);
    }
    
    // Execute authenticated request
    private void executeAuthenticatedRequest(Request request, ApiCallback<?> callback, RequestType requestType) {
        if (!hasValidToken()) {
            // Need to authenticate first
            authenticate(new ApiCallback<OAuth2TokenResponse>() {
                @Override
                public void onSuccess(OAuth2TokenResponse token) {
                    // Retry the original request with the new token
                    Request authenticatedRequest = request.newBuilder()
                            .addHeader(WillowApiV3Config.AUTH_HEADER, 
                                      WillowApiV3Config.AUTH_BEARER_PREFIX + token.getAccessToken())
                            .build();
                    
                    executeRequest(authenticatedRequest, callback, requestType);
                }
                
                @Override
                public void onError(String error) {
                    callback.onError(error);
                }
            });
        } else {
            // Use existing token
            Request authenticatedRequest = request.newBuilder()
                    .addHeader(WillowApiV3Config.AUTH_HEADER, 
                              WillowApiV3Config.AUTH_BEARER_PREFIX + currentToken.getAccessToken())
                    .build();
            
            executeRequest(authenticatedRequest, callback, requestType);
        }
    }
    
    // Execute the actual HTTP request
    private void executeRequest(Request request, ApiCallback<?> callback, RequestType requestType) {
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Request failed: " + request.url(), e);
                callback.onError(WillowApiV3Config.ERROR_NETWORK + ": " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseBody = response.body().string();
                    
                    if (response.isSuccessful()) {
                        Object result = parseResponse(responseBody, requestType);
                        ((ApiCallback<Object>) callback).onSuccess(result);
                    } else {
                        Log.e(TAG, "API error: " + response.code() + " " + responseBody);
                        
                        if (response.code() == 401) {
                            // Token expired, clear it
                            currentToken = null;
                            callback.onError(WillowApiV3Config.ERROR_TOKEN_EXPIRED);
                        } else {
                            callback.onError(WillowApiV3Config.ERROR_API + ": " + response.code() + " " + response.message());
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error processing response", e);
                    callback.onError(WillowApiV3Config.ERROR_PARSING + ": " + e.getMessage());
                } finally {
                    response.close();
                }
            }
        });
    }
    
    // Parse response based on request type
    private Object parseResponse(String responseBody, RequestType requestType) {
        switch (requestType) {
            case TWINS_LIST:
                // V3 API returns paginated response with 'content' field
                try {
                    TwinsPageResponse pageResponse = gson.fromJson(responseBody, TwinsPageResponse.class);
                    return pageResponse.content != null ? pageResponse.content : new ArrayList<WillowTwinV3>();
                } catch (Exception e) {
                    // Fallback to direct list parsing
                    Type twinsListType = new TypeToken<List<WillowTwinV3>>(){}.getType();
                    return gson.fromJson(responseBody, twinsListType);
                }
                
            case TWIN_SINGLE:
                return gson.fromJson(responseBody, WillowTwinV3.class);
                
            case TIME_SERIES_LIST:
                Type timeSeriesListType = new TypeToken<List<WillowTimeSeriesData>>(){}.getType();
                return gson.fromJson(responseBody, timeSeriesListType);
                
            case INSIGHTS_LIST:
                Type insightsListType = new TypeToken<List<WillowInsight>>(){}.getType();
                return gson.fromJson(responseBody, insightsListType);
                
            case TICKETS_LIST:
                Type ticketsListType = new TypeToken<List<WillowTicket>>(){}.getType();
                return gson.fromJson(responseBody, ticketsListType);
                
            default:
                return responseBody;
        }
    }
    
    // Get twins with advanced filtering
    public void getTwins(TwinsQueryRequest queryRequest, ApiCallback<List<WillowTwinV3>> callback) {
        String requestBody = gson.toJson(queryRequest);
        
        RequestBody body = RequestBody.create(
                requestBody, 
                MediaType.parse(WillowApiV3Config.CONTENT_TYPE_JSON)
        );
        
        Request request = new Request.Builder()
                .url(baseUrl + WillowApiV3Config.ENDPOINT_TWINS)
                .post(body)
                .addHeader(WillowApiV3Config.CONTENT_TYPE_HEADER, WillowApiV3Config.CONTENT_TYPE_JSON)
                .build();

        Log.d(TAG, "Getting twins with query: " + requestBody);
        executeAuthenticatedRequest(request, (ApiCallback<?>) callback, RequestType.TWINS_LIST);
    }
    
    // Get twin by ID
    public void getTwinById(String twinId, boolean includeRelationships, ApiCallback<WillowTwinV3> callback) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl + WillowApiV3Config.ENDPOINT_TWINS + "/" + twinId).newBuilder();
        
        if (includeRelationships) {
            urlBuilder.addQueryParameter(WillowApiV3Config.PARAM_INCLUDE_RELATIONSHIPS, "true");
        }
        
        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .get()
                .build();

        Log.d(TAG, "Getting twin by ID: " + twinId);
        executeAuthenticatedRequest(request, (ApiCallback<?>) callback, RequestType.TWIN_SINGLE);
    }
    
    // Get time series data using the V3 API structure
    public void getTimeSeries(String twinId, String startTime, String endTime, int pageSize, ApiCallback<List<WillowTimeSeriesData>> callback) {
        executeWithAuth((token) -> {
            HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl + "/time-series/" + twinId).newBuilder()
                    .addQueryParameter("start", startTime)
                    .addQueryParameter("end", endTime)
                    .addQueryParameter("pageSize", String.valueOf(pageSize))
                    .addQueryParameter("includeDataQuality", "true");
            
            Request request = new Request.Builder()
                    .url(urlBuilder.build())
                    .get()
                    .addHeader(WillowApiV3Config.AUTH_HEADER, WillowApiV3Config.AUTH_BEARER_PREFIX + token.getAccessToken())
                    .build();

            Log.d(TAG, "Getting time series for twin: " + twinId + " URL: " + urlBuilder.build());
            
            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Failed to get time series", e);
                    callback.onError(WillowApiV3Config.ERROR_NETWORK + ": " + e.getMessage());
                }
                
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String responseBody = response.body().string();
                        Log.d(TAG, "Time series response: " + responseBody);
                        
                        if (response.isSuccessful()) {
                            // Parse the response - it might be wrapped in a data object
                            TimeSeriesResponse tsResponse = gson.fromJson(responseBody, TimeSeriesResponse.class);
                            if (tsResponse != null && tsResponse.data != null) {
                                callback.onSuccess(tsResponse.data);
                            } else {
                                // Try parsing as direct array
                                Type timeSeriesListType = new TypeToken<List<WillowTimeSeriesData>>(){}.getType();
                                List<WillowTimeSeriesData> timeSeries = gson.fromJson(responseBody, timeSeriesListType);
                                callback.onSuccess(timeSeries != null ? timeSeries : new ArrayList<>());
                            }
                        } else {
                            Log.e(TAG, "Time series API error: " + response.code() + " " + responseBody);
                            if (response.code() == 401) {
                                currentToken = null;
                                callback.onError(WillowApiV3Config.ERROR_TOKEN_EXPIRED);
                            } else {
                                callback.onError("API Error: " + response.code() + " " + response.message());
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing time series response", e);
                        callback.onError(WillowApiV3Config.ERROR_PARSING + ": " + e.getMessage());
                    } finally {
                        response.close();
                    }
                }
            });
        }, callback);
    }

    // Get latest time series value
    public void getLatestTimeSeries(String twinId, ApiCallback<List<WillowTimeSeriesData>> callback) {
        executeWithAuth((token) -> {
            HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl + "/time-series/" + twinId + "/latest").newBuilder()
                    .addQueryParameter("includeDataQuality", "true");
            
            Request request = new Request.Builder()
                    .url(urlBuilder.build())
                    .get()
                    .addHeader(WillowApiV3Config.AUTH_HEADER, WillowApiV3Config.AUTH_BEARER_PREFIX + token.getAccessToken())
                    .build();

            Log.d(TAG, "Getting latest time series for twin: " + twinId);
            
            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Failed to get latest time series", e);
                    callback.onError(WillowApiV3Config.ERROR_NETWORK + ": " + e.getMessage());
                }
                
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String responseBody = response.body().string();
                        Log.d(TAG, "Latest time series response: " + responseBody);
                        
                        if (response.isSuccessful()) {
                            // Parse as array of time series data
                            Type timeSeriesListType = new TypeToken<List<WillowTimeSeriesData>>(){}.getType();
                            List<WillowTimeSeriesData> timeSeries = gson.fromJson(responseBody, timeSeriesListType);
                            callback.onSuccess(timeSeries != null ? timeSeries : new ArrayList<>());
                        } else {
                            Log.e(TAG, "Latest time series API error: " + response.code() + " " + responseBody);
                            if (response.code() == 401) {
                                currentToken = null;
                                callback.onError(WillowApiV3Config.ERROR_TOKEN_EXPIRED);
                            } else {
                                callback.onError("API Error: " + response.code() + " " + response.message());
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing latest time series response", e);
                        callback.onError(WillowApiV3Config.ERROR_PARSING + ": " + e.getMessage());
                    } finally {
                        response.close();
                    }
                }
            });
        }, callback);
    }
    
    // Response wrapper for time series data
    public static class TimeSeriesResponse {
        public List<WillowTimeSeriesData> data;
        public String continuationToken;
        public ErrorData errorData;
        
        public static class ErrorData {
            public String message;
            public List<String> ids;
        }
    }
    
    // Response wrapper for paginated twins response
    public static class TwinsPageResponse {
        public List<WillowTwinV3> content;
        public String continuationToken;
    }
    
    // Get insights with filtering
    public void getInsights(InsightsQueryRequest queryRequest, ApiCallback<List<WillowInsight>> callback) {
        String requestBody = gson.toJson(queryRequest);
        
        RequestBody body = RequestBody.create(
                requestBody, 
                MediaType.parse(WillowApiV3Config.CONTENT_TYPE_JSON)
        );
        
        Request request = new Request.Builder()
                .url(baseUrl + WillowApiV3Config.ENDPOINT_INSIGHTS)
                .post(body)
                .addHeader(WillowApiV3Config.CONTENT_TYPE_HEADER, WillowApiV3Config.CONTENT_TYPE_JSON)
                .build();

        Log.d(TAG, "Getting insights with query: " + requestBody);
        executeAuthenticatedRequest(request, (ApiCallback<?>) callback, RequestType.INSIGHTS_LIST);
    }
    
    // Get tickets with filtering
    public void getTickets(TicketsQueryRequest queryRequest, ApiCallback<List<WillowTicket>> callback) {
        String requestBody = gson.toJson(queryRequest);
        
        RequestBody body = RequestBody.create(
                requestBody, 
                MediaType.parse(WillowApiV3Config.CONTENT_TYPE_JSON)
        );
        
        Request request = new Request.Builder()
                .url(baseUrl + WillowApiV3Config.ENDPOINT_TICKETS)
                .post(body)
                .addHeader(WillowApiV3Config.CONTENT_TYPE_HEADER, WillowApiV3Config.CONTENT_TYPE_JSON)
                .build();

        Log.d(TAG, "Getting tickets with query: " + requestBody);
        executeAuthenticatedRequest(request, (ApiCallback<?>) callback, RequestType.TICKETS_LIST);
    }
    
    // Helper method to search for buildings by name
    // Search for buildings by name using the new V3 API structure
    public void searchBuildingByName(String buildingName, ApiCallback<List<WillowTwinV3>> callback) {
        Log.d(TAG, "Searching for buildings containing: " + buildingName);
        
        // Create a search request using POST /twins with filters
        TwinsQueryRequest queryRequest = new TwinsQueryRequest();
        
        // Set model filter for buildings - try multiple building-related models
        ModelFilter modelFilter = new ModelFilter();
        modelFilter.modelIds = java.util.Arrays.asList(
            WillowApiV3Config.MODEL_BUILDING,
            "dtmi:com:willowinc:Space;1", // Include Space model as buildings might be categorized as spaces
            "dtmi:com:willowinc:Site;1"   // Include Site model as well
        );
        modelFilter.exactModelMatch = false; // Allow broader matching
        queryRequest.modelFilter = modelFilter;
        
        // Set property filter to search by multiple name fields
        PropertyFilter propertyFilter = new PropertyFilter();
        
        // Create multiple filters for different name fields
        List<FilterSpecification> nameFilters = new ArrayList<>();
        
        // Search in 'name' property
        FilterSpecification nameFilter = new FilterSpecification();
        nameFilter.field = "name";
        nameFilter.operator = WillowApiV3Config.FILTER_CONTAINS;
        nameFilter.value = buildingName;
        nameFilters.add(nameFilter);
        
        // Search in 'displayName' property (common in twin definitions)
        FilterSpecification displayNameFilter = new FilterSpecification();
        displayNameFilter.field = "displayName";
        displayNameFilter.operator = WillowApiV3Config.FILTER_CONTAINS;
        displayNameFilter.value = buildingName;
        nameFilters.add(displayNameFilter);
        
        // Search in 'contents.name' (nested property)
        FilterSpecification contentsNameFilter = new FilterSpecification();
        contentsNameFilter.field = "contents.name";
        contentsNameFilter.operator = WillowApiV3Config.FILTER_CONTAINS;
        contentsNameFilter.value = buildingName;
        nameFilters.add(contentsNameFilter);
        
        propertyFilter.filters = nameFilters;
        propertyFilter.filterConditionOperator = WillowApiV3Config.FILTER_OR; // Use OR to search across all name fields
        queryRequest.propertyFilter = propertyFilter;
        
        // Include relationships for complete data
        IncludeOption include = new IncludeOption();
        include.outgoingRelationships = true;
        include.incomingRelationships = true;
        queryRequest.include = include;
        
        queryRequest.pageSize = 50;
        
        getTwins(queryRequest, callback);
    }
    
    // Get all buildings without specific filters (for broader search)
    public void getAllBuildings(ApiCallback<List<WillowTwinV3>> callback) {
        Log.d(TAG, "Getting all building-related twins");
        
        TwinsQueryRequest queryRequest = new TwinsQueryRequest();
        
        // Set model filter for building-related models
        ModelFilter modelFilter = new ModelFilter();
        modelFilter.modelIds = Arrays.asList(
            WillowApiV3Config.MODEL_BUILDING,
            "dtmi:com:willowinc:Space;1",
            "dtmi:com:willowinc:Site;1",
            WillowApiV3Config.MODEL_LEVEL
        );
        modelFilter.exactModelMatch = false;
        queryRequest.modelFilter = modelFilter;
        
        // Include relationships for complete data
        IncludeOption include = new IncludeOption();
        include.outgoingRelationships = true;
        include.incomingRelationships = true;
        queryRequest.include = include;
        
        queryRequest.pageSize = 100;
        
        getTwins(queryRequest, callback);
    }
    
    // Get all available models
    public void getModels(ApiCallback<List<Object>> callback) {
        executeWithAuth((token) -> {
            Request request = new Request.Builder()
                    .url(baseUrl + WillowApiV3Config.ENDPOINT_MODELS)
                    .get()
                    .addHeader(WillowApiV3Config.AUTH_HEADER, WillowApiV3Config.AUTH_BEARER_PREFIX + token.getAccessToken())
                    .build();

            Log.d(TAG, "Getting models from: " + baseUrl + WillowApiV3Config.ENDPOINT_MODELS);
            
            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Failed to get models", e);
                    callback.onError(WillowApiV3Config.ERROR_NETWORK + ": " + e.getMessage());
                }
                
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String responseBody = response.body().string();
                        Log.d(TAG, "Models response: " + responseBody);
                        
                        if (response.isSuccessful()) {
                            // Parse as array of objects
                            Type modelsListType = new TypeToken<List<Object>>(){}.getType();
                            List<Object> models = gson.fromJson(responseBody, modelsListType);
                            callback.onSuccess(models != null ? models : new ArrayList<>());
                        } else {
                            Log.e(TAG, "Models API error: " + response.code() + " " + responseBody);
                            if (response.code() == 401) {
                                currentToken = null;
                                callback.onError(WillowApiV3Config.ERROR_TOKEN_EXPIRED);
                            } else {
                                callback.onError("API Error: " + response.code() + " " + response.message());
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing models response", e);
                        callback.onError(WillowApiV3Config.ERROR_PARSING + ": " + e.getMessage());
                    } finally {
                        response.close();
                    }
                }
            });
        }, callback);
    }

    

    
    // Enum for request types
    private enum RequestType {
        TWINS_LIST,
        TWIN_SINGLE,
        TIME_SERIES_LIST,
        INSIGHTS_LIST,
        TICKETS_LIST
    }
    
    // Query request classes
    public static class TwinsQueryRequest {
        public ModelFilter modelFilter;
        public RelationshipFilter relationshipFilter;
        public LocationFilter locationFilter;
        public DateFilter dateFilter;
        public PropertyFilter propertyFilter;
        public IncludeOption include;
        public int pageSize = WillowApiV3Config.DEFAULT_PAGE_SIZE;
        public String continuationToken;
    }
    
    public static class ModelFilter {
        public List<String> modelIds;
        public boolean exactModelMatch = true;
    }
    
    public static class RelationshipFilter {
        public String relationshipDirection;
        public List<String> relationshipNames;
        public ModelFilter relatedModelFilter;
    }
    
    public static class LocationFilter {
        public String locationId;
    }
    
    public static class DateFilter {
        public String lastUpdatedAfter;
        public String lastUpdatedBefore;
    }
    
    public static class PropertyFilter {
        public List<FilterSpecification> filters;
        public String filterConditionOperator = WillowApiV3Config.FILTER_AND;
    }
    
    public static class FilterSpecification {
        public String field;
        public String operator;
        public Object value;
        public String filterConditionOperator = WillowApiV3Config.FILTER_AND;
    }
    
    public static class IncludeOption {
        public boolean outgoingRelationships = false;
        public boolean incomingRelationships = false;
    }
    
    public static class InsightsQueryRequest {
        public List<FilterSpecification> filterSpecifications;
        public List<SortSpecification> sortSpecifications;
        public int page = 0;
        public int pageSize = WillowApiV3Config.DEFAULT_PAGE_SIZE;
    }
    
    public static class TicketsQueryRequest {
        public List<FilterSpecification> filterSpecifications;
        public List<SortSpecification> sortSpecifications;
        public int page = 0;
        public int pageSize = WillowApiV3Config.DEFAULT_PAGE_SIZE;
    }
    
    public static class SortSpecification {
        public String field;
        public String sort; // "asc" or "desc"
    }
}
