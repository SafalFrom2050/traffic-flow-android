package com.example.trafficflow.ui.bottomsheets.Model

import com.google.gson.annotations.SerializedName

data class VehicleType(
    @SerializedName("is_public")
    var isPublic: Boolean?,
    var type: String?
)
