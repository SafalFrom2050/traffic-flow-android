package com.example.trafficflow.falsereport.Model

data class FalseReportResponse(
    var falseReports: FalseReport?,
    var count: Int?,
    var message: String?
) {
    constructor(message: String) : this(null, null, message)
}
