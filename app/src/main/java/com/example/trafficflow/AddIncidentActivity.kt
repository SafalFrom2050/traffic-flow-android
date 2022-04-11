package com.example.trafficflow

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.trafficflow.databinding.ActivityAddIncidentBinding
import com.example.trafficflow.ui.incident.Model.Incident
import com.example.trafficflow.ui.incident.Repository.IncidentRepository
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

val INCIDENT_ID = "incident_id"
val INCIDENT_NAME = "incident_name"
val INCIDENT_IMAGE_URL = "incident_image_url"

class ReportActivity : AppCompatActivity(), View.OnClickListener {

    var TAG = "AddIncidentActivity"
    lateinit var binding: ActivityAddIncidentBinding
    lateinit var incidentImageUrl: String
    lateinit var incidentName: String
    lateinit var incidentId: String
    val incidentRepository = IncidentRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddIncidentBinding.inflate(layoutInflater)

        setContentView(binding.root)

        incidentImageUrl = intent.getStringExtra(INCIDENT_IMAGE_URL).toString()
        incidentName = intent.getStringExtra(INCIDENT_NAME).toString()
        incidentId = intent.getStringExtra(INCIDENT_ID).toString()

        val y = Glide.with(this)
            .load(RetrofitInstance.BASE_URL + incidentImageUrl)
            .into(binding.include.imageIncident)

        binding.include.nameIncident.text = incidentName

        binding.btnSubmit.setOnClickListener(this)
    }

    override fun onClick(v: View) {

        Utils.hideSoftKeyBoard(this, v)


        val name = binding.fieldName.text.toString()
        val description = binding.fieldDescription.text.toString()
        val latitude = "0"
        val longitude = "0"
        val deviceIdentifier = "test"
        val severity = binding.severityRangeSlider.value.toInt()
        val incidentId = this.incidentId.toInt()
        val userId = RetrofitInstance.currentUser.id

        val incident = Incident(
            name,
            description,
            latitude,
            longitude,
            deviceIdentifier,
            severity,
            incidentId,
            userId
        )

        lifecycleScope.launch {
            incidentRepository.addIncident(incident)
        }

        incidentRepository.addIncidentResponseLiveData.observe(this) {
            if (it.incident != null) {
                val snackBar = Snackbar.make(
                    v,
                    "Thank You for helping the Flow community!", Snackbar.LENGTH_SHORT
                )
                    .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                        override fun onShown(transientBottomBar: Snackbar?) {
                            super.onShown(transientBottomBar)
                        }

                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            super.onDismissed(transientBottomBar, event)
                            this@ReportActivity.finish()
                        }
                    })

                snackBar.show()
            } else {
                Snackbar.make(
                    v,
                    it.message!!.capitalize(), Snackbar.LENGTH_SHORT
                ).setBackgroundTint(resources.getColor(R.color.error)).show()
            }
        }

        incidentRepository.isLoadingLiveData.observe(this) {
            binding.btnSubmit.isEnabled = !it
        }

    }


}