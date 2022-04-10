package com.example.trafficflow.auth

import com.example.trafficflow.auth.Model.AuthResponse
import com.example.trafficflow.auth.Model.LoginModel
import com.example.trafficflow.auth.Model.RegisterModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

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
}