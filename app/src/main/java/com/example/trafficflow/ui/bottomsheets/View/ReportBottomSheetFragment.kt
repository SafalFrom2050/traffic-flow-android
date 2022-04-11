package com.example.trafficflow.ui.bottomsheets.View

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trafficflow.*
import com.example.trafficflow.ui.bottomsheets.Adapter.IncidentTypesAdapter
import com.example.trafficflow.ui.bottomsheets.Model.IncidentType
import com.example.trafficflow.ui.bottomsheets.ViewModel.ReportBottomSheetViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar

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