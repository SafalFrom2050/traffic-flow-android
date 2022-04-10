package com.example.trafficflow.ui.bottomsheets

import com.example.trafficflow.ui.bottomsheets.Model.IncidentTypesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers

interface IncidentTypeApi {

    @Headers(
        "Accept: application/json",
        "User-Agent: TrafficFlow",
    )
    @GET("incident_types")
    suspend fun getIncidentTypes(@Header("Authorization") accessToken: String): Response<IncidentTypesResponse>
}