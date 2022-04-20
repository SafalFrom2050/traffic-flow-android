package com.example.trafficflow

import android.content.Context
import com.example.trafficflow.auth.AuthApi
import com.example.trafficflow.auth.Model.User
import com.example.trafficflow.auth.Repository.UserRepository
import com.example.trafficflow.falsereport.FalseReportApi
import com.example.trafficflow.services.TripModeApi
import com.example.trafficflow.ui.achievements.AchievementApi
import com.example.trafficflow.ui.bottomsheets.IncidentTypeApi
import com.example.trafficflow.ui.incident.IncidentApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    const val BASE_URL = "https://app-traffic-flow.herokuapp.com/"
    var accessToken: String = ""
    var currentUser: User = User()


    fun retrieveAccessToken(context: Context): String {
        accessToken = "Bearer " + UserRepository().getAccessTokenFromSP(context).toString()
        return accessToken
    }

    val authApi: AuthApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL + "api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }

    val incidentTypeApi: IncidentTypeApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL + "api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IncidentTypeApi::class.java)
    }

    val incidentApi: IncidentApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL + "api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IncidentApi::class.java)
    }

    val achievementApi: AchievementApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL + "api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AchievementApi::class.java)
    }

    val tripModeApi: TripModeApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL + "api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TripModeApi::class.java)
    }

    val falseReportApi: FalseReportApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL + "api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FalseReportApi::class.java)
    }
}