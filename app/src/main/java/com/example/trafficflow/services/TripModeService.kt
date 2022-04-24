package com.example.trafficflow.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.example.trafficflow.MainActivity
import com.example.trafficflow.R
import com.example.trafficflow.RetrofitInstance
import com.example.trafficflow.roadtrip.Model.LocationData
import com.example.trafficflow.roadtrip.Model.RoadTrip
import com.example.trafficflow.roadtrip.Model.RoadTripResponse
import com.example.trafficflow.roadtrip.Model.RoadTripsResponse
import com.example.trafficflow.roadtrip.Repository.RoadTripRepository
import com.example.trafficflow.ui.bottomsheets.Model.VehicleResponse
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.MapboxNavigationProvider
import com.mapbox.navigation.core.trip.session.LocationMatcherResult
import com.mapbox.navigation.core.trip.session.LocationObserver
import com.mapbox.navigation.core.trip.session.TripSessionState
import com.mapbox.navigation.core.trip.session.TripSessionStateObserver
import com.mapbox.navigation.utils.internal.NOTIFICATION_ID
import com.mapbox.navigation.utils.internal.toPoint
import kotlinx.coroutines.launch

class TripModeService : LifecycleService() {

    companion object {
        val roadTripLiveData = MutableLiveData<RoadTripResponse?>(null)
        val isOnTripModeLiveData = MutableLiveData<Boolean>(false)
        val isVehicleSelectedLiveData = MutableLiveData<Boolean>(false)
        val warningLiveData = MutableLiveData<String?>(null)
        val vehicleResponseLiveData = MutableLiveData<VehicleResponse>(null)
    }

    val TAG = "TripModeService"
    val NOTIFICATION_CHANNEL_ID = "trip_mode_notification_channel"
    val NOTIFICATION_CHANNEL_NAME = "Trip Mode"

    lateinit var mapboxNavigation: MapboxNavigation

    val roadTripRepository = RoadTripRepository()

    private var locationObserver = object : LocationObserver {

        override fun onNewLocationMatcherResult(locationMatcherResult: LocationMatcherResult) {
            Log.d(TAG, locationMatcherResult.keyPoints.toString())
            if (locationMatcherResult.roadEdgeMatchProbability < 0.8){
                warningLiveData.value = "Are you sure you are on the road?"
            }else{
                if (warningLiveData.value != null){
                    warningLiveData.value = null
                }
            }
            handleNewLocation(locationMatcherResult);

        }

        override fun onNewRawLocation(rawLocation: Location) {
            //TODO
        }
    }

    private fun handleNewLocation(locationMatcherResult: LocationMatcherResult) {
        if (roadTripLiveData.value == null && roadTripRepository.isLoadingLiveData.value != null && !roadTripRepository.isLoadingLiveData.value!!) {
            createRoadTrip("Kathmandu")
        }else{
            linkToTripIdAndPushLocationData(
                LocationData(
                    latitude = locationMatcherResult.enhancedLocation.latitude.toString(),
                    longitude = locationMatcherResult.enhancedLocation.longitude.toString(),
                    road_trip_id = roadTripLiveData.value?.roadTrip?.id,
                    speed = locationMatcherResult.enhancedLocation.speed.toString()
                )
            )
        }
    }

    private fun createRoadTrip(startingPoint: String) {
        val roadTrip = RoadTrip(
            user_id = RetrofitInstance.currentUser.id,
            vehicle_id = null,
            starting_point = startingPoint
        )
        Log.d(TAG, "Creating road trip: " + roadTrip)

        lifecycleScope.launch {
            roadTripRepository.createRoadTrips(roadTrip)
        }

        roadTripRepository.createdRoadTripLiveData.observe(this) {
            if(it?.roadTrip != null) {
                roadTripLiveData.value = it
            }
        }
    }

    private fun linkToTripIdAndPushLocationData(locationData: LocationData) {
        locationData.road_trip_id = roadTripLiveData.value?.roadTrip?.id
        lifecycleScope.launch {
            roadTripRepository.pushLocationData(locationData)
        }
    }

    private var tripSessionStateObserver = TripSessionStateObserver {
        if (it.name == TripSessionState.STOPPED.name) {
            stopSelf()
        }
    }

    @SuppressLint("MissingPermission")
    private fun initMapboxNavigation() {
        mapboxNavigation = MapboxNavigationProvider.retrieve()
        mapboxNavigation.apply {
            startTripSession()
            registerLocationObserver(locationObserver)
        }
        mapboxNavigation.registerTripSessionStateObserver(tripSessionStateObserver)
    }



    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getActivity(this, 0, notificationIntent, 0)
        }

        createNotification(pendingIntent)
        initMapboxNavigation()
        isOnTripModeLiveData.postValue(true)
        return START_NOT_STICKY
    }

    private fun createNotification(pendingIntent: PendingIntent) {
        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("In Trip Mode")
            .setContentText("--:--")
            .setSmallIcon(R.drawable.ic_trip)
            .setAutoCancel(false)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .build()


        startForeground(NOTIFICATION_ID, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {

        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )

        notificationManager.createNotificationChannel(channel)
    }

    override fun onDestroy() {
        super.onDestroy()
        roadTripLiveData.postValue(null)
        warningLiveData.postValue(null)
        isOnTripModeLiveData.postValue(false)
        isVehicleSelectedLiveData.postValue(false)

        mapboxNavigation.stopTripSession()
        mapboxNavigation.unregisterLocationObserver(locationObserver)
    }
}