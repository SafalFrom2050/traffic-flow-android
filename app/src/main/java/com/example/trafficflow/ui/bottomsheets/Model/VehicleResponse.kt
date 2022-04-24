package com.example.trafficflow.ui.bottomsheets.Model

import com.google.gson.annotations.SerializedName

data class VehicleResponse (
    val count: Int?,

    @SerializedName("vehicle")
    val vehicle: Vehicle?,
    val message: String?
) {
    constructor(message: String) : this(null,null, message)
}