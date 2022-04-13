package com.example.trafficflow.roadtrip.Model

data class RoadTrip(
    val destination: String?,
    val id: Int?,
    val location_data: List<LocationData>?,
    val starting_point: String?,
    val user_id: Int?,
    val vehicle_id: Int?,

    val created_at: String?,
    val updated_at: String?

) {
    constructor(user_id: Int?, vehicle_id: Nothing?, starting_point: String) : this(null, null, null, starting_point, user_id, vehicle_id, null, null)
}