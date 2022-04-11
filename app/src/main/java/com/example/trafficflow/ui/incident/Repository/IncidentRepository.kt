package com.example.trafficflow.ui.incident.Repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.trafficflow.RetrofitInstance
import com.example.trafficflow.ui.incident.Model.AddIncidentResponse
import com.example.trafficflow.ui.incident.Model.Incident
import com.google.gson.Gson
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class IncidentRepository {
    private val TAG = "IncidentRepository"

    val isLoadingLiveData = MutableLiveData(false)
    val addIncidentResponseLiveData = MutableLiveData<AddIncidentResponse>()

    suspend fun addIncident(incident: Incident): Boolean {
        isLoadingLiveData.value = true
        val accessToken: String = RetrofitInstance.accessToken

        val response = try {
            RetrofitInstance.incidentApi.addIncident(accessToken, incident)
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
            addIncidentResponseLiveData.value = response.body()
            return true
        } else {
            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
            Log.d(TAG, jsonObj.toString())
            addIncidentResponseLiveData.value =
                Gson().fromJson(jsonObj.toString(), AddIncidentResponse::class.java)
        }

        return false
    }

    private fun postUnexpectedError(
        disableLoading: Boolean,
        message: String = "There was an unexpected error!"
    ) {
        addIncidentResponseLiveData.value = AddIncidentResponse(message)
        if (disableLoading) {
            isLoadingLiveData.value = false
        }
    }
}