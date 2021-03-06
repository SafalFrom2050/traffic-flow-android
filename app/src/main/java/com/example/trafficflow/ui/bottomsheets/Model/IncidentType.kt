package com.example.trafficflow.ui.bottomsheets.Model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class IncidentType(
    val id: Int?,
    val name: String,
    val image: String,



    @SerializedName("default_severity")
    val defaultSeverity: Int,

    @SerializedName("created_at")
    val createdAt: Any?,

    @SerializedName("updated_at")
    val updatedAt: Any?,

    val marker: String?
) {
    constructor(name: String, image: String, defaultSeverity: Int) : this(null, name, image, defaultSeverity, null, null, null)
}