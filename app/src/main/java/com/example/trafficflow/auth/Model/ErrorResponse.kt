package com.example.trafficflow.auth.Model

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("fname")
    val fName: List<String>?,

    @SerializedName("lname")
    val lName: List<String>?,
    val phone: List<String>?,
    val email: List<String>?,
    val password: List<String>?,

    @SerializedName("device_name")
    val deviceName: List<String>?
)