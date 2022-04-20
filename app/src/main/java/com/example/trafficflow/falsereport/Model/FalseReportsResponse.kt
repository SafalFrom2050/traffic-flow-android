package com.example.trafficflow.falsereport.Model

import com.google.gson.annotations.SerializedName

data class FalseReportsResponse(
    @SerializedName("false_reports")
    var falseReports: List<FalseReport>?,
    var count: Int?,
    var message: String?
) {
    constructor(message: String) : this(null, null, message)
}
