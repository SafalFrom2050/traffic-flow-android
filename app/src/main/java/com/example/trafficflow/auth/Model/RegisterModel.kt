package com.example.trafficflow.auth.Model

import com.google.gson.annotations.SerializedName

data class RegisterModel (
    val fname: String,
    val lname: String,
    val phone: String,
    val email: String,
    val password: String,

    @SerializedName("device_name")
    val deviceName: String
)