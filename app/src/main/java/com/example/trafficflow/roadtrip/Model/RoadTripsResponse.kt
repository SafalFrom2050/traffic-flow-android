package com.example.trafficflow.roadtrip.Model

data class RoadTripsResponse(
    val roadTrips: List<RoadTrip>?,
    val message: String?,
    val count: Int?,
) {
    constructor(message: String) : this(null, message, null)
}
