package com.example.trafficflow.auth.Model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("created_at")
    val createdAt: String,
    val dob: String,
    val email: String,

    @SerializedName("profile_image")
    val profileImage: String,

    @SerializedName("email_verified_at")
    val emailVerifiedAt: String,
    val fname: String,
    val id: Int?,

    @SerializedName("is_approved")
    val isApproved: Boolean?,
    val lname: String,
    val phone: String,

    @SerializedName("reward_points")
    val rewardPoints: Int,
    val title: String,

    @SerializedName("updated_at")
    val updatedAt: String,
    val username: Any
) {
    constructor() : this("", "", "", "","", "", null, null, "", "", 0, "", "", "")
}