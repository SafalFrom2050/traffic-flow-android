package com.example.trafficflow.roadtrip.Repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.trafficflow.RetrofitInstance
import com.example.trafficflow.roadtrip.Model.LocationData
import com.example.trafficflow.roadtrip.Model.RoadTrip
import com.example.trafficflow.roadtrip.Model.RoadTripResponse
import com.example.trafficflow.roadtrip.Model.RoadTripsResponse
import com.google.gson.Gson
import com.mapbox.geojson.GeoJson
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class RoadTripRepository {
    private val TAG = "RoadTripRepository"

    val isLoadingLiveData = MutableLiveData<Boolean>(false)
    val roadTripsResponseLiveData = MutableLiveData<RoadTripsResponse>()
    val createdRoadTripLiveData = MutableLiveData<RoadTripResponse>()
    val geoJsonLiveData = MutableLiveData<String>()

    suspend fun getGeoJson(): Boolean {
        val accessToken: String = RetrofitInstance.accessToken

        val response = try {
            RetrofitInstance.tripModeApi.getGeojson(accessToken)
        }catch (e: IOException) {
            Log.e(TAG, "IO Exception: " + e.message)
            postUnexpectedError(true, "" + e.message)
            return false
        } catch (e: HttpException) {
            Log.e(TAG, "Http Exception")
            postUnexpectedError(true)
            return false
        }

        isLoadingLiveData.value = false

        if (response.isSuccessful) {
            geoJsonLiveData.value = response.body()
            return true
        } else {
            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
            roadTripsResponseLiveData.value =
                Gson().fromJson(jsonObj.toString(), RoadTripsResponse::class.java)        }

        return false
    }

    suspend fun getRoadTrips(): Boolean {
        val accessToken: String = RetrofitInstance.accessToken

        val response = try {
            RetrofitInstance.tripModeApi.getRoadTrips(accessToken)
        }catch (e: IOException) {
            Log.e(TAG, "IO Exception: " + e.message)
            postUnexpectedError(true, "" + e.message)
            return false
        } catch (e: HttpException) {
            Log.e(TAG, "Http Exception")
            postUnexpectedError(true)
            return false
        }

        isLoadingLiveData.value = false

        if (response.isSuccessful) {
            roadTripsResponseLiveData.value = response.body()
            return true
        } else {
            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
            roadTripsResponseLiveData.value =
                Gson().fromJson(jsonObj.toString(), RoadTripsResponse::class.java)        }

        return false
    }

    suspend fun createRoadTrips(roadTrip: RoadTrip): Boolean {
        val accessToken: String = RetrofitInstance.accessToken

        val response = try {
            RetrofitInstance.tripModeApi.createRoadTrip(accessToken, roadTrip)
        }catch (e: IOException) {
            Log.e(TAG, "IO Exception: " + e.message)
            postUnexpectedError(true, "" + e.message)
            return false
        } catch (e: HttpException) {
            Log.e(TAG, "Http Exception")
            postUnexpectedError(true)
            return false
        }

        isLoadingLiveData.value = false

        if (response.isSuccessful) {
            createdRoadTripLiveData.value = response.body()
            return true
        } else {
            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
            roadTripsResponseLiveData.value =
                Gson().fromJson(jsonObj.toString(), RoadTripsResponse::class.java)        }

        return false
    }

    suspend fun pushLocationData(locationData: LocationData): Boolean {
        val accessToken: String = RetrofitInstance.accessToken

        val response = try {
            RetrofitInstance.tripModeApi.pushLocationData(accessToken, locationData)
        }catch (e: IOException) {
            Log.e(TAG, "IO Exception: " + e.message)
            postUnexpectedError(true, "" + e.message)
            return false
        } catch (e: HttpException) {
            Log.e(TAG, "Http Exception")
            postUnexpectedError(true)
            return false
        }

        isLoadingLiveData.value = false

        // Don't care about response body
        if (response.isSuccessful) {
            return true
        }
        return false
    }


    private fun postUnexpectedError(
        disableLoading: Boolean,
        message: String = "There was an unexpected error!"
    ) {
        roadTripsResponseLiveData.value = RoadTripsResponse(message)
        if (disableLoading) {
            isLoadingLiveData.value = false
        }
    }
}