package com.example.trafficflow.ui.achievements.Model

import com.google.gson.annotations.SerializedName

data class Achievement(
    val id: Int,
    val name: String,
    val level: Int,

    @SerializedName("points_required")
    val pointsRequired: Int,

    @SerializedName("updated_at")
    val updatedAt: Any,

    @SerializedName("created_at")
    val createdAt: Any,
)