package com.example.trafficflow.ui.bottomsheets.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trafficflow.ui.bottomsheets.Repository.IncidentTypeRepository
import kotlinx.coroutines.launch

class ReportBottomSheetViewModel: ViewModel() {

    private val TAG = "ReportBottomSheetViewModel"

    val incidentTypesRepository = IncidentTypeRepository()


    fun loadIncidentTypes() {
        viewModelScope.launch {
            incidentTypesRepository.getIncidentTypes()
        }
    }

}