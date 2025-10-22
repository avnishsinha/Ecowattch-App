package com.example.ecowattchtechdemo.willow;

import android.util.Log;
import com.example.ecowattchtechdemo.willow.models.*;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import java.util.concurrent.TimeUnit;

/**
 * Client factory for Willow API
 */
public class WillowApiClient {
    
    private static final String TAG = "WillowApiClient";
    private static WillowApiService apiService;
    private static String baseUrl = WillowApiV3Config.DEFAULT_BASE_URL;
    
    /**
     * Get Willow API service instance
     */
    public static WillowApiService getApiService() {
        if (apiService == null) {
            apiService = createApiService(baseUrl);
        }
        return apiService;
    }
    
    /**
     * Get API service with custom base URL
     */
    public static WillowApiService getApiService(String customBaseUrl) {
        return createApiService(customBaseUrl);
    }
    
    /**
     * Update base URL and recreate service
     */
    public static void setBaseUrl(String newBaseUrl) {
        baseUrl = newBaseUrl;
        apiService = null; // Force recreation on next call
    }
    
    /**
     * Create API service instance
     */
    private static WillowApiService createApiService(String baseUrlParam) {
        try {
            // Create HTTP client with logging
            OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()
                    .connectTimeout(WillowApiV3Config.CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
                    .readTimeout(WillowApiV3Config.READ_TIMEOUT, TimeUnit.MILLISECONDS)
                    .writeTimeout(WillowApiV3Config.WRITE_TIMEOUT, TimeUnit.MILLISECONDS);
            
            // Add logging interceptor for debugging
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                httpClientBuilder.addInterceptor(loggingInterceptor);
            }
            
            OkHttpClient httpClient = httpClientBuilder.build();
            
            // Create Retrofit instance
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(WillowApiV3Config.getApiUrl(baseUrlParam) + "/")
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            
            Log.d(TAG, "Created Willow API service with base URL: " + baseUrlParam);
            return retrofit.create(WillowApiService.class);
            
        } catch (Exception e) {
            Log.e(TAG, "Error creating Willow API service", e);
            throw new RuntimeException("Failed to create Willow API service", e);
        }
    }
    
    /**
     * Test connection to API
     */
    public static boolean testConnection() {
        // This would be implemented to do a simple health check
        // For now, return true if service can be created
        try {
            getApiService();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Connection test failed", e);
            return false;
        }
    }
}
