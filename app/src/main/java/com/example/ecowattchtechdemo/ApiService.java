package com.example.ecowattchtechdemo;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.http.Path;
import com.example.ecowattchtechdemo.willow.models.EnergyDataResponse;

public interface ApiService {
    @POST("/login")
    Call<LoginResponse> login(@Body LoginRequest request);
    
    // Willow API integration endpoints (for future use)
    @GET("/energy/{buildingId}")
    Call<EnergyDataResponse> getEnergyData(@Path("buildingId") String buildingId);
}
