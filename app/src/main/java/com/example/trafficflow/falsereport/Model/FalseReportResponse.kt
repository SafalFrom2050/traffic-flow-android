package com.example.trafficflow.falsereport.Model

import com.google.gson.annotations.SerializedName

data class FalseReportResponse(
    @SerializedName("false_report")
    var falseReport: FalseReport?,
    var count: Int?,
    var message: String?
) {
    constructor(message: String) : this(null, null, message)
}
