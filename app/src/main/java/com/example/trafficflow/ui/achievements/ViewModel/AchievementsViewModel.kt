package com.example.trafficflow.ui.achievements.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trafficflow.ui.achievements.Repository.AchievementRepository
import kotlinx.coroutines.launch

class AchievementsViewModel: ViewModel() {

    val achievementRepository = AchievementRepository()

    fun getAchievements() {
        viewModelScope.launch {
            achievementRepository.getAchievements()
        }
    }
}