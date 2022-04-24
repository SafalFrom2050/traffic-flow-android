package com.example.trafficflow.ui.bottomsheets.Model

import com.example.trafficflow.roadtrip.Model.RoadTrip
import com.google.gson.annotations.SerializedName

data class Vehicle(
    var id: String?,
    var name: String?,
    var make: String?,
    var model: String?,

    @SerializedName("registration_no")
    var registrationNo: String?,

    var description: String?,

    @SerializedName("top_speed")
    var topSpeed: String?,

    @SerializedName("vehicle_type_id")
    var vehicleTypeId: String?,

    @SerializedName("vehicle_type")
    var vehicleType: VehicleType?,

    @SerializedName("road_trip_id")
    var roadTripId: String?,

    @SerializedName("road_trip")
    var roadTrip: RoadTrip?
) {
    constructor(name: String, vehicleType: VehicleType, roadTripId: String?) : this(
        id = null, name,
        make = null,
        model = null,
        registrationNo = null,
        description = null,
        topSpeed = null,
        vehicleTypeId = null,
        vehicleType = vehicleType,
        roadTripId = roadTripId,
        roadTrip = null
    )
}