package com.example.trafficflow

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.trafficflow.databinding.ActivityAddIncidentBinding
import com.example.trafficflow.databinding.ActivityFalseReportBinding
import com.example.trafficflow.falsereport.Model.FalseReport
import com.example.trafficflow.falsereport.Repository.FalseReportRepository
import com.example.trafficflow.ui.incident.Repository.IncidentRepository
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.MapboxNavigationProvider
import com.mapbox.navigation.core.trip.session.LocationMatcherResult
import com.mapbox.navigation.core.trip.session.LocationObserver
import com.mapbox.navigation.utils.internal.toPoint
import kotlinx.coroutines.launch

class FalseReportActivity : AppCompatActivity(), View.OnClickListener {
    var TAG = "FalseReportActivity"

    companion object {
        const val INCIDENT_ID = "incident_id"
        const val INCIDENT_IMAGE_URL = "incident_image_url"
        const val INCIDENT_NAME = "incident_name"
        const val INCIDENT_LATITUDE = "incident_latitude"
        const val INCIDENT_LONGITUDE = "incident_longitude"
    }

    val falseReportReportRepository = FalseReportRepository()
    lateinit var binding: ActivityFalseReportBinding


    lateinit var incidentImageUrl: String
    lateinit var incidentName: String
    lateinit var incidentId: String
    lateinit var incidentLatitude: String
    lateinit var incidentLongitude: String

    lateinit var locationPoint: Point

    lateinit var mapboxNavigation: MapboxNavigation




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_false_report)
        binding = ActivityFalseReportBinding.inflate(layoutInflater)

        setContentView(binding.root)

        incidentImageUrl = intent.getStringExtra(INCIDENT_IMAGE_URL).toString()
        incidentName = intent.getStringExtra(INCIDENT_NAME).toString()
        incidentId = intent.getIntExtra(INCIDENT_ID, 0).toString()
        incidentLatitude = intent.getStringExtra(INCIDENT_LATITUDE).toString()
        incidentLongitude = intent.getStringExtra(INCIDENT_LONGITUDE).toString()

        val y = Glide.with(this)
            .load(RetrofitInstance.BASE_URL + incidentImageUrl)
            .into(binding.include.imageIncident)

        binding.include.nameIncident.text = incidentName

        binding.btnSubmit.setOnClickListener(this)

        initMapboxNavigation()
        setMapboxStyle()

        initLocationComponent()

        setIncidentLocationOnMap(Point.fromLngLat(incidentLongitude.toDouble(), incidentLatitude.toDouble()))
    }

    @SuppressLint("MissingPermission")
    private fun initMapboxNavigation() {
        mapboxNavigation = MapboxNavigationProvider.retrieve()
        mapboxNavigation.apply {
            startTripSession()
            registerLocationObserver(locationObserver)
        }
    }


    private fun initLocationComponent() {
        val locationComponentPlugin = binding.mapView.location

        locationComponentPlugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
//        locationComponentPlugin.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
    }


    private var locationObserver = object : LocationObserver {

        override fun onNewLocationMatcherResult(locationMatcherResult: LocationMatcherResult) {
            locationPoint = locationMatcherResult.enhancedLocation.toPoint()
            binding.btnSubmit.isEnabled = true
            // location point only needed once for this activity
            mapboxNavigation.stopTripSession()
            mapboxNavigation.unregisterLocationObserver(this)

        }

        override fun onNewRawLocation(rawLocation: Location) {
            //TODO
        }
    }

    private fun setIncidentLocationOnMap(point: Point): Boolean {

        val annotationManager = binding.mapView.annotations
        annotationManager.cleanup()
        val pointAnnotationManager = annotationManager.createPointAnnotationManager()

        Glide.with(this).asBitmap().load(RetrofitInstance.BASE_URL + incidentImageUrl).into(object : CustomTarget<Bitmap?>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {
                val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                    .withPoint(point)
                    .withIconImage(resource)
                    .withIconSize(1.0)
                    .withDraggable(true)

                pointAnnotationManager.create(pointAnnotationOptions)
            }
            override fun onLoadCleared(placeholder: Drawable?) {}
        })

        locationPoint = point
        binding.btnSubmit.isEnabled = true

        return true
    }



    private fun setMapboxStyle() {
        val mapbox = binding.mapView.getMapboxMap()

        if (isDarkModeOn()){
            mapbox.loadStyleUri(Style.TRAFFIC_NIGHT)
        }else {
            mapbox.loadStyleUri(
                Style.TRAFFIC_DAY
            )
            // After the style is loaded, initialize the Location component.
            {
                binding.mapView.location.updateSettings {
                    enabled = true
                    pulsingEnabled = true
                }
            }
        }
    }


    private fun isDarkModeOn(): Boolean {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }


    private fun setupGesturesListener() {
        binding.mapView.gestures.addOnMoveListener(onMoveListener)
    }

    private val onIndicatorBearingChangedListener = OnIndicatorBearingChangedListener {
        binding.mapView.getMapboxMap().setCamera(CameraOptions.Builder().bearing(it).build())
    }

    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
        binding.mapView.getMapboxMap().setCamera(CameraOptions.Builder().center(it).build())
        binding.mapView.gestures.focalPoint = binding.mapView.getMapboxMap().pixelForCoordinate(it)
    }

    private fun onCameraTrackingDismissed() {
        binding.mapView.location
            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        binding.mapView.location
            .removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        binding.mapView.gestures.removeOnMoveListener(onMoveListener)
    }


    private val onMoveListener = object : OnMoveListener {
        override fun onMoveBegin(detector: MoveGestureDetector) {
            onCameraTrackingDismissed()
        }

        override fun onMove(detector: MoveGestureDetector): Boolean {
            return false
        }

        override fun onMoveEnd(detector: MoveGestureDetector) {}
    }


    override fun onDestroy() {
        super.onDestroy()
        mapboxNavigation.stopTripSession()
    }




    override fun onClick(v: View) {

        val details = binding.fieldDetails.text.toString()
        val userId = RetrofitInstance.currentUser.id.toString()

        val falseReport = FalseReport(details, userId, incidentId)

        binding.btnSubmit.isEnabled = false
        lifecycleScope.launch {
            falseReportReportRepository.addFalseReport(falseReport)
        }
        falseReportReportRepository.falseReportResponseLiveData.observe(this){
            if (it.falseReport != null) {
                binding.btnSubmit.isEnabled = false
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
                            this@FalseReportActivity.finish()
                        }
                    })

                snackBar.show()
            } else if (it.message != null){
                Snackbar.make(
                    v,
                    it.message.toString().capitalize(), Snackbar.LENGTH_SHORT
                ).setBackgroundTint(resources.getColor(R.color.error)).show()
                binding.btnSubmit.isEnabled = true
            }
        }
    }
}