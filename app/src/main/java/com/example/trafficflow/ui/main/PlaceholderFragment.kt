package com.example.trafficflow.ui.main

import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.trafficflow.R
import com.example.trafficflow.RetrofitInstance
import com.example.trafficflow.databinding.FragmentMainBinding
import com.example.trafficflow.roadtrip.Repository.RoadTripRepository
import com.example.trafficflow.ui.incident.Model.Incident
import com.example.trafficflow.ui.incident.Repository.IncidentRepository
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.*
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.expressions.dsl.generated.*
import com.mapbox.maps.extension.style.expressions.generated.Expression.Companion.linear
import com.mapbox.maps.extension.style.expressions.generated.Expression.Companion.rgba
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.circleLayer
import com.mapbox.maps.extension.style.layers.generated.heatmapLayer
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.layers.properties.generated.LineCap
import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.navigation.core.MapboxNavigationProvider
import com.mapbox.navigation.core.directions.session.RoutesObserver
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineOptions
import com.mapbox.navigation.ui.maps.route.line.model.RouteLine
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineColorResources
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineResources


import kotlinx.coroutines.launch

/**
 * A placeholder fragment containing a simple view.
 */
class PlaceholderFragment : Fragment() {

    private lateinit var pageViewModel: PageViewModel

    lateinit var binding: FragmentMainBinding
    val roadTripRepository = RoadTripRepository()

    val mapboxNavigation by lazy {
        MapboxNavigationProvider.retrieve()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProvider(this).get(PageViewModel::class.java).apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMainBinding.inflate(inflater, container, false)
        val root = binding.root

        val textView: TextView = binding.sectionLabel
        pageViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setMapboxStyle()

        // Order matters
        initLocationComponent()
        setupGesturesListener()
        //

        getIncidents()
    }

    private fun initLocationComponent() {
        val locationComponentPlugin = binding.mapView.location
        locationComponentPlugin.updateSettings {
            this.enabled = true
            this.locationPuck = LocationPuck2D(
                bearingImage = AppCompatResources.getDrawable(
                    requireActivity(),
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
        locationComponentPlugin.addOnIndicatorPositionChangedListener(
            onIndicatorPositionChangedListener
        )
        //locationComponentPlugin.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)

//        lifecycleScope.launch {
//            roadTripRepository.getGeoJson()
//        }

        binding.mapView.getMapboxMap().getStyle {
            it.addSource(
                geoJsonSource("traffic"){
                    url(RetrofitInstance.BASE_URL + "api/geojson")
                }
            )

            it.addLayer(heatmapLayer("heatmap-layer", "traffic"){



                heatmapIntensity(
                    interpolate {
                        linear()
                        zoom()
                        stop {
                            literal(0)
                            literal(0)
                        }
                        stop {
                            literal(9)
                            literal(1)
                        }
                    }
                )

                heatmapRadius(
                    interpolate {
                        linear()
                        get("speed")
                        stop(1.0) {
                            literal(1.0)
                        }
                        stop(100.0) {
                            literal(20.0)
                        }
                    }
                )
            })
        }
    }

    private fun getCustomFeatureCollection(): FeatureCollection {

        val points: List<Point> = arrayListOf(
            Point.fromLngLat(85.4208976, 27.6610444),
            Point.fromLngLat(85.41982430569306, 27.662396724465182),
            Point.fromLngLat(85.42093623501711, 27.66365782852897)
        )


        val features: MutableList<Feature> = arrayListOf()
        points.forEach{
            features.add(Feature.fromGeometry(it))
        }

        return FeatureCollection.fromFeatures(features)
    }

    private fun getIncidents() {
        val incidentRepository = IncidentRepository()

        lifecycleScope.launch {
            incidentRepository.getIncidents()
        }

        incidentRepository.incidentResponseLiveData.observe(viewLifecycleOwner) {
            if (it.incidents != null && it.incidents.isNotEmpty()) {
                addIncidentAnnotations(it.incidents)
            }
        }
    }

    private fun addIncidentAnnotations(incidents: List<Incident>) {
        incidents.forEach {
            Log.d("MAIN FRAGMENT", it.incidentType?.image.toString())
            val annotationManager = binding.mapView.annotations
            val pointAnnotationManager = annotationManager.createPointAnnotationManager()

            Glide.with(this).asBitmap().circleCrop().load(
                RetrofitInstance.BASE_URL + (it.incidentType?.marker
                    ?: "storage/images/default-marker.png")
            ).into(object : CustomTarget<Bitmap?>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap?>?
                ) {
                    val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                        .withPoint(
                            Point.fromLngLat(
                                it.longitude?.toDouble() ?: 0.0,
                                it.latitude?.toDouble() ?: 0.0
                            )
                        )
                        .withIconImage(resource)
                        .withIconSize(1.0)

                    pointAnnotationManager.create(pointAnnotationOptions)
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })

        }
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


    private fun setMapboxStyle() {
        val mapbox = binding.mapView.getMapboxMap()

        val style: String = if (isDarkModeOn()) {
            Style.TRAFFIC_NIGHT
        } else {
            Style.TRAFFIC_DAY
        }

        mapbox.loadStyleUri(style)
        {
            binding.mapView.location.updateSettings {
                enabled = true
                pulsingEnabled = true
            }
        }

    }


    private fun isDarkModeOn(): Boolean {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(sectionNumber: Int): PlaceholderFragment {
            return PlaceholderFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }

}