package com.example.trafficflow.ui.achievements.Repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.trafficflow.RetrofitInstance
import com.example.trafficflow.ui.achievements.Model.AchievementsResponse
import com.example.trafficflow.ui.bottomsheets.Model.IncidentTypesResponse
import com.google.gson.Gson
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class AchievementRepository {
    private val TAG = "AchievementRepository"

    val isLoadingLiveData = MutableLiveData<Boolean>(false)
    val achievementResponseLiveData = MutableLiveData<AchievementsResponse>()

    suspend fun getAchievements(): Boolean {
        val accessToken: String = RetrofitInstance.accessToken

        val response = try {
            RetrofitInstance.achievementApi.getAchievements(accessToken)
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
            achievementResponseLiveData.value = response.body()
            return true
        } else {
            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
            achievementResponseLiveData.value =
                Gson().fromJson(jsonObj.toString(), AchievementsResponse::class.java)        }

        return false
    }

    private fun postUnexpectedError(
        disableLoading: Boolean,
        message: String = "There was an unexpected error!"
    ) {
        achievementResponseLiveData.value = AchievementsResponse(message)
        if (disableLoading) {
            isLoadingLiveData.value = false
        }
    }
}