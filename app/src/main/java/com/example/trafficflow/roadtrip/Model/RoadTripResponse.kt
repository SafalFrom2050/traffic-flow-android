package com.example.trafficflow.roadtrip.Model

data class RoadTripResponse(
    val roadTrip: RoadTrip?,
    val message: String?,
    val count: Int?,
) {
    constructor(message: String) : this(null, message, null)
}
