package com.example.trafficflow

import android.content.Context
import com.example.trafficflow.auth.AuthApi
import com.example.trafficflow.auth.Repository.UserRepository
import com.example.trafficflow.ui.achievements.AchievementApi
import com.example.trafficflow.ui.bottomsheets.IncidentTypeApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val authApi: AuthApi by lazy {
        Retrofit.Builder()
            .baseUrl("http://192.168.1.66:8000/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }

    val incidentTypeApi: IncidentTypeApi by lazy {
        Retrofit.Builder()
            .baseUrl("http://192.168.1.66:8000/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IncidentTypeApi::class.java)
    }

    val achievementApi: AchievementApi by lazy {
        Retrofit.Builder()
            .baseUrl("http://192.168.1.66:8000/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AchievementApi::class.java)
    }

    var accessToken: String = ""

    fun setAccessToken(context: Context): String {
        accessToken = "Bearer " + UserRepository().getAccessTokenFromSP(context).toString()
        return accessToken
    }

}