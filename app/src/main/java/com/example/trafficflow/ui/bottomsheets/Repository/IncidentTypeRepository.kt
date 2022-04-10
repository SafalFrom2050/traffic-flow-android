package com.example.trafficflow.ui.bottomsheets.Repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.trafficflow.RetrofitInstance
import com.example.trafficflow.auth.Repository.UserRepository
import com.example.trafficflow.ui.bottomsheets.Model.IncidentTypesResponse
import com.google.gson.Gson
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class IncidentTypeRepository {
    private val TAG = "IncidentTypeRepository"

    val isLoadingLiveData = MutableLiveData<Boolean>(false)
    val incidentTypeResponseLiveData = MutableLiveData<IncidentTypesResponse>()

    suspend fun getIncidentTypes(): Boolean {
        val accessToken: String = RetrofitInstance.accessToken

        val response = try {
            RetrofitInstance.incidentTypeApi.getIncidentTypes(accessToken)
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
            incidentTypeResponseLiveData.value = response.body()
            return true
        } else {
            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
            incidentTypeResponseLiveData.value =
                Gson().fromJson(jsonObj.toString(), IncidentTypesResponse::class.java)        }

        return false
    }

    private fun postUnexpectedError(
        disableLoading: Boolean,
        message: String = "There was an unexpected error!"
    ) {
        incidentTypeResponseLiveData.value = IncidentTypesResponse(message)
        if (disableLoading) {
            isLoadingLiveData.value = false
        }
    }
}