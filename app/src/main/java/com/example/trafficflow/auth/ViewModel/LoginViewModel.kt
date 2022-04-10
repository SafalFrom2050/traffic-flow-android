package com.example.trafficflow.auth.ViewModel

import android.content.Context
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trafficflow.auth.Repository.UserRepository
import kotlinx.coroutines.launch


class LoginViewModel : ViewModel() {
    private val TAG = "LoginViewModel"

    val emailLiveData = MutableLiveData<String>()
    val passwordLiveData = MutableLiveData<String>()
    val isValidLiveData = MediatorLiveData<Boolean>().apply {
        // default value
        this.value = false

        // Validate on email field changes
        addSource(emailLiveData) { email ->
            val password = passwordLiveData.value
            this.value = validateForm(email, password)
        }

        // Validate on password field changes
        addSource(passwordLiveData) { password ->
            val email = emailLiveData.value
            this.value = validateForm(email, password)
        }
    }

    val userRepository = UserRepository()


    private fun validateForm(email: String?, password: String?): Boolean {
        val isValidEmail = email != null && email.isNotBlank() && email.contains("@")
        val isValidPassword = password != null && password.isNotBlank() && password.length >= 4

        return isValidEmail && isValidPassword
    }

    fun logIn(context: Context) {
        viewModelScope.launch {
            userRepository.logIn(context, emailLiveData.value!!, passwordLiveData.value!!)
        }
    }
}