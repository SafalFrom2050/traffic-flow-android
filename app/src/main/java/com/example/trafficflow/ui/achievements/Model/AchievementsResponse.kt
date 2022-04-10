package com.example.trafficflow.ui.achievements.Model

data class AchievementsResponse(
    val achievements: List<Achievement>?,
    val count: Int?,
    val message: String?
) {
    constructor(message: String) : this(null, null, message)
}