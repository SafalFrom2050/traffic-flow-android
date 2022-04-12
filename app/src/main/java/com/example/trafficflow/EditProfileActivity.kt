package com.example.trafficflow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.example.trafficflow.databinding.ActivityEditProfileBinding

class EditProfileActivity : AppCompatActivity() {

    lateinit var binding: ActivityEditProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpToolbar()
        loadUser()
    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun loadUser() {
        val user = RetrofitInstance.currentUser
        if (user.id != null) {
            binding.fieldEditFirstName.setText(user.fname)
            binding.fieldEditLastName.setText(user.lname)
            binding.fieldEditEmail.setText(user.email)
            binding.fieldEditPhone.setText(user.phone)
        }

        Glide.with(this).load(RetrofitInstance.BASE_URL + user.profileImage).centerCrop()
            .into(binding.profileImage)
    }
}