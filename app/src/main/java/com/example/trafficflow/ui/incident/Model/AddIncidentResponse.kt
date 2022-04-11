package com.example.trafficflow.ui.incident.Model

data class AddIncidentResponse(
    val incident: Incident?,
    val message: String?
){
    constructor(message: String): this(null, message)
}