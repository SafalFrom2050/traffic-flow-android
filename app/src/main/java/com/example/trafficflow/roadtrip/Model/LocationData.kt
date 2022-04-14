package com.example.trafficflow.roadtrip.Model

data class LocationData(
    var id: Int?,
    var latitude: String,
    var longitude: String,
    var speed: String = "unspecified",

    var road_trip_id: Int?,

    var updated_at: String?,
    var created_at: String?,
    var device_identifier: String = "unspecified",

    ) {
    constructor(latitude: String, longitude: String, road_trip_id: Int?, speed: String) : this(
        null,
        latitude,
        longitude,
        speed,
        road_trip_id = road_trip_id,
        updated_at = null,
        created_at = null
    )
}