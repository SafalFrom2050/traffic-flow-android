package com.example.trafficflow.ui.achievements

import com.example.trafficflow.ui.achievements.Model.AchievementsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers

interface AchievementApi {
    @Headers(
        "Accept: application/json",
        "User-Agent: TrafficFlow",
    )
    @GET("achievements")
    suspend fun getAchievements(@Header("Authorization") accessToken: String): Response<AchievementsResponse>
}