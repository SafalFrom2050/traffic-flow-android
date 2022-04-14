package com.example.trafficflow.falsereport

import com.example.trafficflow.falsereport.Model.FalseReport
import com.example.trafficflow.falsereport.Model.FalseReportResponse
import com.example.trafficflow.falsereport.Model.FalseReportsResponse
import com.example.trafficflow.roadtrip.Model.RoadTrip
import com.example.trafficflow.roadtrip.Model.RoadTripResponse
import retrofit2.Response
import retrofit2.http.*

interface FalseReportApi {

    @Headers(
        "Accept: application/json",
        "User-Agent: TrafficFlow",
    )
    @POST("false_reports")
    suspend fun addFalseReport(@Header("Authorization") accessToken: String, @Body falseReport: FalseReport): Response<FalseReportResponse>

    @Headers(
        "Accept: application/json",
        "User-Agent: TrafficFlow",
    )
    @GET("false_reports")
    suspend fun getFalseReports(@Header("Authorization") accessToken: String): Response<FalseReportsResponse>

}