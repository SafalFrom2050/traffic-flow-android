package com.example.trafficflow.auth

import com.example.trafficflow.auth.Model.AuthResponse
import com.example.trafficflow.auth.Model.LoginModel
import com.example.trafficflow.auth.Model.RegisterModel
import com.example.trafficflow.auth.Model.User
import retrofit2.Response
import retrofit2.http.*

interface AuthApi {

    @Headers(
        "Accept: application/json",
        "User-Agent: TrafficFlow",
    )
    @POST("user/login/")
    suspend fun signIn(@Body loginModel: LoginModel): Response<AuthResponse>

    @Headers(
        "Accept: application/json",
        "User-Agent: TrafficFlow",
    )
    @POST("user/signup/")
    suspend fun register(@Body registerModel: RegisterModel): Response<AuthResponse>

    @Headers(
        "Accept: application/json",
        "User-Agent: TrafficFlow",
    )
    @GET("user")
    suspend fun getCurrentUser(@Header("Authorization") accessToken: String): Response<User>
}