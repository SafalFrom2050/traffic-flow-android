package com.example.trafficflow.falsereport.Model

import com.example.trafficflow.auth.Model.User
import com.google.gson.annotations.SerializedName

data class FalseReport(
    var id: String?,
    var details: String?,

    @SerializedName("user_id")
    var userId: String?,

    var user: User?,

    @SerializedName("incident_id")
    var incidentId: String?,
) {
    constructor(details: String?, userId: String?, incidentId: String?) : this(null, details, userId, null, incidentId)
}
