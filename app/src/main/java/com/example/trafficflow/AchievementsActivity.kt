package com.example.trafficflow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trafficflow.databinding.ActivityAchievementsBinding
import com.example.trafficflow.ui.achievements.Adapter.AchievementsAdapter
import com.example.trafficflow.ui.achievements.Model.Achievement
import com.example.trafficflow.ui.achievements.ViewModel.AchievementsViewModel
import com.google.android.material.snackbar.Snackbar

class AchievementsActivity : AppCompatActivity() {

    lateinit var viewModel: AchievementsViewModel

    private lateinit var binding: ActivityAchievementsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAchievementsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[AchievementsViewModel::class.java]

        viewModel.getAchievements()


        setUpToolbar()
        setUpRewards()
        setUpRV()
        setErrorMessages()
    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setUpRewards() {
        binding.labelReward.text = RetrofitInstance.currentUser.rewardPoints.toString()
    }

    private fun setUpRV() {
        val recyclerView = binding.rvAchievements

        recyclerView.layoutManager = LinearLayoutManager(this)

        val achievements = ArrayList<Achievement>()
        val adapter = AchievementsAdapter(achievements)

        viewModel.achievementRepository.achievementResponseLiveData.observe(this) {
            if (it.achievements != null) {
                it.achievements.let { it1 -> achievements.addAll(it1) }
                adapter.notifyItemRangeChanged(adapter.itemCount-1, it.achievements.size -1)
            }
        }

        recyclerView.adapter = adapter
    }

    private fun setErrorMessages() {
        viewModel.achievementRepository.achievementResponseLiveData.observe(this) {
            if (it.achievements == null && it.message != null) {
                Snackbar.make(binding.root,
                    it.message.capitalize(), Snackbar.LENGTH_SHORT).setBackgroundTint(resources.getColor(R.color.error)).show()
            }
        }
    }
}