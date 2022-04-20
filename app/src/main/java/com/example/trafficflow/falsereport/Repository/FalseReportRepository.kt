package com.example.trafficflow.falsereport.Repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.trafficflow.RetrofitInstance
import com.example.trafficflow.falsereport.Model.FalseReport
import com.example.trafficflow.falsereport.Model.FalseReportResponse
import com.example.trafficflow.falsereport.Model.FalseReportsResponse
import com.google.gson.Gson
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class FalseReportRepository {

    private val TAG = "FalseReportRepository"

    val isLoadingLiveData = MutableLiveData<Boolean>(false)
    val falseReportResponseLiveData = MutableLiveData<FalseReportResponse>()
    val falseReportsResponseLiveData = MutableLiveData<FalseReportsResponse>()

    suspend fun getFalseReports(incidentId: String): Boolean {
        val accessToken: String = RetrofitInstance.accessToken

        Log.d("API", incidentId.toString())
        val response = try {
            RetrofitInstance.falseReportApi.getFalseReports(accessToken, incidentId)
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
            falseReportsResponseLiveData.value = response.body()
            return true
        } else {
            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
            falseReportsResponseLiveData.value =
                Gson().fromJson(jsonObj.toString(), FalseReportsResponse::class.java)
        }

        return false
    }

    suspend fun addFalseReport(falseReport: FalseReport): Boolean {
        val accessToken: String = RetrofitInstance.accessToken
        Log.d("API", falseReport.toString());

        isLoadingLiveData.value = true

        val response = try {
            RetrofitInstance.falseReportApi.addFalseReport(accessToken, falseReport)
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
            falseReportResponseLiveData.value = response.body()
            return true
        } else {
            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
            falseReportResponseLiveData.value =
                Gson().fromJson(jsonObj.toString(), FalseReportResponse::class.java)        }

        return false
    }

    private fun postUnexpectedError(
        disableLoading: Boolean,
        message: String = "There was an unexpected error!"
    ) {
        falseReportsResponseLiveData.value = FalseReportsResponse(message)
        falseReportResponseLiveData.value = FalseReportResponse(message)
        if (disableLoading) {
            isLoadingLiveData.value = false
        }
    }
}