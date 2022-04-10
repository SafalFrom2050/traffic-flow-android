package com.example.trafficflow.auth.Repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.trafficflow.RetrofitInstance
import com.example.trafficflow.auth.Model.AuthResponse
import com.example.trafficflow.auth.Model.LoginModel
import com.example.trafficflow.auth.Model.RegisterModel
import com.example.trafficflow.auth.View.LoginFragment
import com.google.gson.Gson
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException


private const val SHARED_PREFERENCES_FILE = "trafficflow.app.SharedPreferences"
private const val SHARED_PREFERENCES_KEY_ACCESS_TOKEN = "access_token"
class UserRepository() {
    val TAG = "UserRepository"

    val isLoadingLiveData = MutableLiveData<Boolean>(false)
    val authResponseLiveData = MutableLiveData<AuthResponse>()

    fun getAccessTokenFromSP(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE)
        return sharedPreferences.getString(SHARED_PREFERENCES_KEY_ACCESS_TOKEN, "")
    }

    private fun setAccessTokenToSP(context: Context, accessToken: String) {
        val spEditor = context.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE).edit()
        spEditor?.putString(SHARED_PREFERENCES_KEY_ACCESS_TOKEN, accessToken)
        spEditor?.apply()

        RetrofitInstance.accessToken = "Bearer $accessToken"
    }

    suspend fun logIn(context: Context, email: String, password: String): Boolean {

        isLoadingLiveData.value = true

        val response = try {
            RetrofitInstance.authApi.signIn(
                LoginModel(
                    email, password,
                    "Xiaomi"
                )
            )
        } catch (e: IOException) {
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
            authResponseLiveData.value = response.body()
            RetrofitInstance.currentUser = response.body()!!.user!!
            setAccessTokenToSP(context, response.body()?.accessToken!!)
            return true
        } else {
            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
            authResponseLiveData.value =
                Gson().fromJson(jsonObj.toString(), AuthResponse::class.java)
        }

        return false
    }

    suspend fun register(context: Context, fname: String, lname: String, phone: String, email: String, password: String): Boolean {

        isLoadingLiveData.value = true

        val response = try {
            RetrofitInstance.authApi.register(
                RegisterModel(
                    fname,
                    lname,
                    phone,
                    email,
                    password,
                    "Xiaomi"
                )
            )
        } catch (e: IOException) {
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
            authResponseLiveData.value = response.body()
            RetrofitInstance.currentUser = response.body()!!.user!!
            setAccessTokenToSP(context, response.body()?.accessToken!!)
            return true
        } else {
            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
            authResponseLiveData.value =
                Gson().fromJson(jsonObj.toString(), AuthResponse::class.java)
        }

        return false
    }

    private fun postUnexpectedError(
        disableLoading: Boolean,
        message: String = "There was an unexpected error!"
    ) {
        authResponseLiveData.value = AuthResponse(message)
        if (disableLoading) {
            isLoadingLiveData.value = false
        }
    }
}