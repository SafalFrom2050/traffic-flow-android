package com.example.trafficflow.ui.bottomsheets.View

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trafficflow.*
import com.example.trafficflow.services.TripModeService
import com.example.trafficflow.ui.bottomsheets.Adapter.IncidentTypesAdapter
import com.example.trafficflow.ui.bottomsheets.Model.IncidentType
import com.example.trafficflow.ui.bottomsheets.ViewModel.ReportBottomSheetViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.switchmaterial.SwitchMaterial

class ReportBottomSheetFragment: BottomSheetDialogFragment() {


    lateinit var viewModel: ReportBottomSheetViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun getTheme(): Int {
        return R.style.DialogTheme
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[ReportBottomSheetViewModel::class.java]

        viewModel.loadIncidentTypes()
        setUpReportPageRV()
        setErrorMessages()
        setUpUIListeners()

        Log.d("BottomSheet", "onViewCreated")
    }

    private fun setUpUIListeners() {
        val switchTripMode = requireView().findViewById<SwitchMaterial>(R.id.switchTripMode)
        val labelWarning = requireView().findViewById<TextView>(R.id.labelWarning)
        switchTripMode.setOnCheckedChangeListener { switchBtn, value ->
            val i = Intent (requireContext(), TripModeService::class.java)
            if (value){
                switchBtn.isEnabled = false

                i.let{ intent ->
                   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                       requireActivity().startForegroundService(intent)
                   }else{
                       requireActivity().startService(intent)
                   }
                }

                switchBtn.isEnabled = true
            }else{
                requireActivity().stopService(i)
            }
        }

        // Get states in case the bottom sheet is closed and re-opened
        val isTripMode = TripModeService.isOnTripModeLiveData.value
        val warning = TripModeService.warningLiveData.value


        // Order matters
        setTripWarning(warning, labelWarning, switchTripMode)
        setSwitchMode(isTripMode == true, switchTripMode)

        //

        // Listen for new changes in live data
        TripModeService.isOnTripModeLiveData.observe(this) {
            setSwitchMode(it, switchTripMode)
        }

        TripModeService.warningLiveData.observe(this) {
            setTripWarning(it, labelWarning, switchTripMode)
        }
        //
    }
    private fun setSwitchMode(isTripMode: Boolean, switchTripMode: SwitchMaterial) {
        switchTripMode.isChecked = isTripMode
    }

    private fun setTripWarning(warning: String?, labelWarning: TextView, switchTripMode: SwitchMaterial) {
        if (warning != null){
            labelWarning.visibility = View.VISIBLE
            labelWarning.text = warning
        }else{
            labelWarning.text = ""
            labelWarning.visibility = View.INVISIBLE
        }
    }

    private fun setUpReportPageRV() {
        val recyclerView = requireView().findViewById<RecyclerView>(R.id.rvIncidentTypes)!!

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)

        val incidents = ArrayList<IncidentType>()
        val adapter = IncidentTypesAdapter(incidents) {
            val i = Intent(requireContext(), ReportActivity::class.java)
            i.putExtra(INCIDENT_ID, it.id.toString())
            i.putExtra(INCIDENT_NAME, it.name.toString())
            i.putExtra(INCIDENT_IMAGE_URL, it.image.toString())
            startActivity(i)
        }

        viewModel.incidentTypesRepository.incidentTypeResponseLiveData.observe(viewLifecycleOwner) {
            if (it.incident_types != null) {
                it.incident_types.let { it1 -> incidents.addAll(it1) }
                adapter.notifyItemRangeChanged(adapter.itemCount-1, it.incident_types.size -1)
            }
        }

        recyclerView.adapter = adapter
    }

    private fun setErrorMessages() {
        viewModel.incidentTypesRepository.incidentTypeResponseLiveData.observe(viewLifecycleOwner) {
            if (it.incident_types == null && it.message != null) {
                Snackbar.make(requireView(),
                    it.message.capitalize(), Snackbar.LENGTH_SHORT).setBackgroundTint(resources.getColor(R.color.error)).show()
            }
        }
    }
}