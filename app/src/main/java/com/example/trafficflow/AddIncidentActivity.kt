package com.example.trafficflow

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.trafficflow.databinding.ActivityAddIncidentBinding
import com.example.trafficflow.ui.incident.Model.Incident
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
import com.mapbox.maps.plugin.gestures.OnMapClickListener
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.MapboxNavigationProvider
import com.mapbox.navigation.core.trip.session.LocationMatcherResult
import com.mapbox.navigation.core.trip.session.LocationObserver
import com.mapbox.navigation.utils.internal.toPoint
import kotlinx.coroutines.launch

val INCIDENT_ID = "incident_id"
val INCIDENT_NAME = "incident_name"
val INCIDENT_IMAGE_URL = "incident_image_url"

class ReportActivity : AppCompatActivity(), View.OnClickListener, OnMapClickListener {

    var TAG = "AddIncidentActivity"

    val incidentRepository = IncidentRepository()


    lateinit var binding: ActivityAddIncidentBinding
    lateinit var incidentImageUrl: String
    lateinit var incidentName: String
    lateinit var incidentId: String

    lateinit var locationPoint: Point

    lateinit var mapboxNavigation: MapboxNavigation

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

        initMapboxNavigation()
        setMapboxStyle()

        // Order matters
        initLocationComponent()
        setupGesturesListener()
        //
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
        locationComponentPlugin.updateSettings {
            this.enabled = true
            this.locationPuck = LocationPuck2D(
                bearingImage = AppCompatResources.getDrawable(
                    this@ReportActivity,
                    R.drawable.ic_locate,
                ),
                scaleExpression = interpolate {
                    linear()
                    zoom()
                    stop {
                        literal(0.0)
                        literal(0.6)
                    }
                    stop {
                        literal(20.0)
                        literal(1.0)
                    }
                }.toJson()
            )
        }
        locationComponentPlugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        //locationComponentPlugin.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
    }

    override fun onMapClick(point: Point): Boolean {

        val annotationManager = binding.mapView.annotations
        annotationManager.cleanup()
        val pointAnnotationManager = annotationManager.createPointAnnotationManager()

        val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
            .withPoint(point)
            .withIconImage(bitmapFromDrawableRes(this, R.drawable.ic_locate)!!)
            .withIconSize(2.0)
            .withDraggable(true)

        pointAnnotationManager.create(pointAnnotationOptions)

        locationPoint = point
        binding.btnSubmit.isEnabled = true

        return true
    }

    private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) =
        convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))

    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        if (sourceDrawable == null) {
            return null
        }
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
// copying drawable object to not manipulate on the same reference
            val constantState = sourceDrawable.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }










    private fun setupGesturesListener() {
        binding.mapView.gestures.addOnMapClickListener(this)
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


    override fun onDestroy() {
        super.onDestroy()
        mapboxNavigation.stopTripSession()
        mapboxNavigation.unregisterLocationObserver(locationObserver)
    }





    override fun onClick(v: View) {

        Utils.hideSoftKeyBoard(this, v)


        // TODO: remove name
        val name = this.incidentName
        val description = binding.fieldDescription.text.toString()
        val latitude = locationPoint.latitude().toString()
        val longitude = locationPoint.longitude().toString()
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