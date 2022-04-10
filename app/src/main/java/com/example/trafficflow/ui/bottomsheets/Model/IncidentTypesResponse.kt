package com.example.trafficflow.ui.bottomsheets.Model

data class IncidentTypesResponse(
    val count: Int?,
    val incident_types: List<IncidentType>?,
    val message: String?
) {
    constructor(message: String) : this(null,null, message)
}