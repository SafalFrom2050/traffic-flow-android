package com.example.trafficflow.auth.ViewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trafficflow.auth.Repository.UserRepository
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private val TAG = "RegisterViewModel"
    val fnameLiveData = MutableLiveData<String>()
    val lnameLiveData = MutableLiveData<String>()
    val phoneLiveData = MutableLiveData<String>()
    val emailLiveData = MutableLiveData<String>()
    val passwordLiveData = MutableLiveData<String>()

    val userRepository = UserRepository()

    val isValidLiveData = MediatorLiveData<Boolean>().apply {
        this.value = false

        addSource(fnameLiveData) { fname ->
            this.value = validateForm(fname = fname)
        }

        addSource(lnameLiveData) { lname ->
            this.value = validateForm(lname = lname)
        }

        addSource(phoneLiveData) { phone ->
            this.value = validateForm(phone = phone)
        }

        addSource(emailLiveData) { email ->
            this.value = validateForm(email = email)
        }

        addSource(passwordLiveData) { password ->
            this.value = validateForm(password = password)
        }
    }

    private fun validateForm(
        fname: String? = fnameLiveData.value,
        lname: String? = lnameLiveData.value,
        phone: String? = phoneLiveData.value,
        email: String? = emailLiveData.value,
        password: String? = passwordLiveData.value
    ): Boolean {
        Log.d(TAG, "Here: "+ "$fname, $lname, $phone, $email, $password")

        val isValidFname = fname != null && fname.isNotBlank()
        val isValidLname = lname != null && lname.isNotBlank()
        val isValidPhone = phone != null && phone.isNotBlank() && phone.length >= 10
        val isValidEmail = email != null && email.isNotBlank() && email.contains("@")
        val isValidPassword = password != null && password.isNotBlank() && password.length >= 4


        return isValidFname && isValidLname && isValidPhone && isValidEmail && isValidPassword
    }

    fun register(context: Context): Boolean {
        viewModelScope.launch {
            userRepository.register(
                context,
                fnameLiveData.value!!,
                lnameLiveData.value!!,
                phoneLiveData.value!!,
                emailLiveData.value!!,
                passwordLiveData.value!!)
        }
        return false
    }


}