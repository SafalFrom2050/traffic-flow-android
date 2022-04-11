package com.example.trafficflow.auth.View

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.trafficflow.MainActivity
import com.example.trafficflow.R
import com.example.trafficflow.RetrofitInstance
import com.example.trafficflow.auth.Repository.UserRepository

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
    }
}