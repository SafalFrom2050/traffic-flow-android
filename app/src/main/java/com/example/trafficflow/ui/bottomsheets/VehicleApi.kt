package com.example.trafficflow.ui.bottomsheets

import com.example.trafficflow.ui.bottomsheets.Model.Vehicle
import com.example.trafficflow.ui.bottomsheets.Model.VehicleResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface VehicleApi {
    @Headers(
        "Accept: application/json",
        "User-Agent: TrafficFlow",
    )
    @POST("vehicles")
    suspend fun createVehicle(@Header("Authorization") accessToken: String, @Body vehicle: Vehicle): Response<VehicleResponse>
}