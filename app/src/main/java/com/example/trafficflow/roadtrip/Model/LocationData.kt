package com.example.trafficflow.roadtrip.Model

data class LocationData(
    var created_at: String?,
    var device_identifier: String = "unspecified",
    var id: Int?,
    var latitude: String,
    var longitude: String,
    var road_trip_id: Int?,
    var speed: String = "unspecified",
    var updated_at: String?
)