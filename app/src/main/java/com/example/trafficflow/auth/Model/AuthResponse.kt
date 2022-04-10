package com.example.trafficflow.auth.Model

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("access_token")
    val accessToken: String?,
    val user: User?,
    val message: String?,
    val errors: ErrorResponse?
) {
    constructor(message: String?) : this(null, null, message, null )
}