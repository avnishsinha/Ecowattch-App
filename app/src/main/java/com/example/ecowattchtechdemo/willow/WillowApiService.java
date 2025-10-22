package com.example.ecowattchtechdemo.willow;

import com.example.ecowattchtechdemo.willow.models.*;
import retrofit2.Call;
import retrofit2.http.*;
import java.util.List;
import java.util.Map;

/**
 * Retrofit interface for Willow API v3 endpoints
 */
public interface WillowApiService {
    
    /**
     * Get OAuth2 token
     */
    @FormUrlEncoded
    @POST("oauth2/token")
    Call<WillowOAuthResponse> getOAuthToken(
        @Field("client_id") String clientId,
        @Field("client_secret") String clientSecret,
        @Field("grant_type") String grantType
    );
    
    /**
     * Get twin by ID
     */
    @GET("twins/{twinId}")
    Call<DigitalTwin> getTwinById(
        @Header("Authorization") String authorization,
        @Path("twinId") String twinId,
        @Query("includeRelationships") boolean includeRelationships
    );
    
    /**
     * Search twins with filters
     */
    @POST("twins")
    Call<TwinsResponse> searchTwins(
        @Header("Authorization") String authorization,
        @Body Map<String, Object> searchRequest
    );
    
    /**
     * Get multiple twins by IDs
     */
    @POST("twins/ids")
    Call<List<DigitalTwin>> getTwinsByIds(
        @Header("Authorization") String authorization,
        @Body List<String> twinIds,
        @Query("includeRelationships") boolean includeRelationships
    );
    
    /**
     * Get time series data for a twin
     */
    @GET("time-series/{twinId}")
    Call<TimeSeriesResponse> getTimeSeries(
        @Header("Authorization") String authorization,
        @Path("twinId") String twinId,
        @Query("start") String startTime,
        @Query("end") String endTime,
        @Query("pageSize") int pageSize,
        @Query("includeDataQuality") boolean includeDataQuality
    );
    
    /**
     * Get latest time series values for multiple twins
     */
    @POST("time-series/ids/latest")
    Call<List<TimeSeriesPoint>> getLatestTimeSeriesValues(
        @Header("Authorization") String authorization,
        @Body List<String> twinIds,
        @Query("includeDataQuality") boolean includeDataQuality
    );
    
    /**
     * Get time series data for multiple twins
     */
    @POST("time-series/ids")
    Call<TimeSeriesResponse> getTimeSeriesForMultipleTwins(
        @Header("Authorization") String authorization,
        @Body List<String> twinIds,
        @Query("start") String startTime,
        @Query("end") String endTime,
        @Query("pageSize") int pageSize,
        @Query("includeDataQuality") boolean includeDataQuality
    );
    
    /**
     * Get latest value for a single twin
     */
    @GET("time-series/{twinId}/latest")
    Call<List<TimeSeriesPoint>> getLatestTimeSeries(
        @Header("Authorization") String authorization,
        @Path("twinId") String twinId,
        @Query("includeDataQuality") boolean includeDataQuality
    );
}
