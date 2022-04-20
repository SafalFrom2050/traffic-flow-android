package com.example.trafficflow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.trafficflow.databinding.ActivityFalseReportsBinding
import com.example.trafficflow.falsereport.Adapter.FalseReportsAdapter
import com.example.trafficflow.falsereport.Model.FalseReport
import com.example.trafficflow.falsereport.Repository.FalseReportRepository
import com.example.trafficflow.ui.achievements.Adapter.AchievementsAdapter
import com.example.trafficflow.ui.achievements.Model.Achievement
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class FalseReportsActivity : AppCompatActivity() {

    lateinit var binding: ActivityFalseReportsBinding

    val falseReportRepository = FalseReportRepository()
    lateinit var incidentImageUrl: String
    lateinit var incidentName: String
    lateinit var incidentId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFalseReportsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        incidentImageUrl = intent.getStringExtra(FalseReportActivity.INCIDENT_IMAGE_URL).toString()
        incidentName = intent.getStringExtra(FalseReportActivity.INCIDENT_NAME).toString()
        incidentId = intent.getIntExtra(FalseReportActivity.INCIDENT_ID, 0).toString()

        binding.include.nameIncident.text = incidentName
        val y = Glide.with(this)
            .load(RetrofitInstance.BASE_URL + incidentImageUrl)
            .into(binding.include.imageIncident)

        loadFalseReports()
        setUpRV()
    }

    private fun setUpRV() {
        val recyclerView = binding.rvFalseReports

        recyclerView.layoutManager = LinearLayoutManager(this)

        val falseReports = ArrayList<FalseReport>()
        val adapter = FalseReportsAdapter(falseReports)

        falseReportRepository.falseReportsResponseLiveData.observe(this) {
            if (it.falseReports != null) {
                it.falseReports.let { it1 ->
                    if (it1 != null) {
                        falseReports.addAll(it1)
                    }
                }
                adapter.notifyItemRangeChanged(adapter.itemCount-1, falseReports.size -1)
            } else if (it.message != null){
                Snackbar.make(
                    binding.root,
                    it.message.toString().capitalize(), Snackbar.LENGTH_SHORT
                ).setBackgroundTint(resources.getColor(R.color.error)).show()
            }
        }

        recyclerView.adapter = adapter
    }

    private fun loadFalseReports() {

        lifecycleScope.launch {
            falseReportRepository.getFalseReports(incidentId)
        }
    }
}