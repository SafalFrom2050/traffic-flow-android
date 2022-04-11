package com.example.trafficflow.ui.incident

import com.example.trafficflow.ui.incident.Model.AddIncidentResponse
import com.example.trafficflow.ui.incident.Model.Incident
import com.example.trafficflow.ui.incident.Model.IncidentResponse
import retrofit2.Response
import retrofit2.http.*

interface IncidentApi {
    @Headers(
        "Accept: application/json",
        "User-Agent: TrafficFlow",
    )
    @POST("incidents")
    suspend fun addIncident(@Header("Authorization") accessToken: String, @Body incident: Incident): Response<AddIncidentResponse>
}