package com.example.trafficflow.falsereport.Model

data class FalseReportsResponse(
    var falseReports: List<FalseReport>?,
    var count: Int?,
    var message: String?
) {
    constructor(message: String) : this(null, null, message)
}
