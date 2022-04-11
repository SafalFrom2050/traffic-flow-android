package com.example.trafficflow.ui.incident.Model

data class IncidentResponse(
    val count: Int?,
    val incidents: List<Incident>?,
    val message: String?
){
    constructor(message: String): this(null, null, message)
}