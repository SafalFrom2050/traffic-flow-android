package com.example.trafficflow.ui.incident.Model

data class Incident(
    val id: Int?,
    val name: String?,
    val description: String?,
    val latitude: String?,
    val longitude: String?,
    val device_identifier: String?,
    val severity: Int?,


    val incident_type_id: Int?,
    val user_id: Int?,

    val updated_at: Any?,
    val created_at: Any?,
){
    constructor(
        name: String,
        description: String,
        latitude: String,
        longitude: String,
        device_identifier: String,
        severity: Int,
        incident_type_id: Int,
        user_id: Int
    ): this(null, name, description, latitude, longitude, device_identifier, severity, incident_type_id, user_id, null, null)
}