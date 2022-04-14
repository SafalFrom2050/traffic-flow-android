package com.example.trafficflow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class FalseReportActivity : AppCompatActivity() {

    companion object {
        const val INCIDENT_ID = "incident_id";
    }

    private lateinit var incident_id: String;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_false_report)

        incident_id = intent.getStringExtra(INCIDENT_ID)!!
    }
}