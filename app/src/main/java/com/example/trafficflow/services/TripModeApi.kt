package com.example.trafficflow.services

import android.location.Location
import com.example.trafficflow.auth.Model.User
import com.example.trafficflow.roadtrip.Model.LocationData
import com.example.trafficflow.roadtrip.Model.RoadTrip
import com.example.trafficflow.roadtrip.Model.RoadTripResponse
import com.example.trafficflow.roadtrip.Model.RoadTripsResponse
import com.mapbox.geojson.GeoJson
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.*

interface TripModeApi {

    @Headers(
        "Accept: application/json",
        "User-Agent: TrafficFlow",
    )
    @POST("road_trips")
    suspend fun createRoadTrip(@Header("Authorization") accessToken: String, @Body roadTrip: RoadTrip): Response<RoadTripResponse>

    @Headers(
        "Accept: application/json",
        "User-Agent: TrafficFlow",
    )
    @GET("road_trips")
    suspend fun getRoadTrips(@Header("Authorization") accessToken: String): Response<RoadTripsResponse>

    @Headers(
        "Accept: application/json",
        "User-Agent: TrafficFlow",
    )
    @POST("location_data")
    suspend fun pushLocationData(@Header("Authorization") accessToken: String, @Body locationData: LocationData): Response<Any>

    @Headers(
        "Accept: application/json",
        "User-Agent: TrafficFlow",
    )
    @GET("geojson")
    suspend fun getGeojson(@Header("Authorization") accessToken: String): Response<String>
}