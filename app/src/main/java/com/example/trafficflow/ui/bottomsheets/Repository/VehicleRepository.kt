package com.example.trafficflow.ui.bottomsheets.Repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.trafficflow.RetrofitInstance
import com.example.trafficflow.ui.bottomsheets.Model.Vehicle
import com.example.trafficflow.ui.bottomsheets.Model.VehicleResponse
import com.google.gson.Gson
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class VehicleRepository {

    private val TAG = "VehicleRepository"

    val isLoadingLiveData = MutableLiveData<Boolean>(false)
    val vehicleResponseLiveData = MutableLiveData<VehicleResponse>()

    suspend fun createVehicle(vehicle: Vehicle): Boolean {
        val accessToken: String = RetrofitInstance.accessToken

        Log.d(TAG, vehicle.toString())
        val response = try {
            RetrofitInstance.vehicleApi.createVehicle(accessToken, vehicle)
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
            Log.d(TAG, response.body().toString())
            vehicleResponseLiveData.value = response.body()
            return true
        } else {
            Log.d(TAG, response.errorBody()!!.charStream().readText())
            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
            vehicleResponseLiveData.value =
                Gson().fromJson(jsonObj.toString(), VehicleResponse::class.java)        }

        return false
    }


    private fun postUnexpectedError(
        disableLoading: Boolean,
        message: String = "There was an unexpected error!"
    ) {
        vehicleResponseLiveData.value = VehicleResponse(message)
        if (disableLoading) {
            isLoadingLiveData.value = false
        }
    }
}