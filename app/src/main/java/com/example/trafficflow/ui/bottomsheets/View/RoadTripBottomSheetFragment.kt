package com.example.trafficflow.ui.bottomsheets.View

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.trafficflow.R
import com.example.trafficflow.databinding.BottomSheetRoadTripFragmentBinding
import com.example.trafficflow.services.TripModeService
import com.example.trafficflow.ui.bottomsheets.Model.Vehicle
import com.example.trafficflow.ui.bottomsheets.Model.VehicleType
import com.example.trafficflow.ui.bottomsheets.Repository.VehicleRepository
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch


class RoadTripBottomSheetFragment : BottomSheetDialogFragment(), View.OnClickListener {

    lateinit var binding: BottomSheetRoadTripFragmentBinding
    val vehicleRepository = VehicleRepository()

    lateinit var roadTripId: String

//    override fun getTheme(): Int {
//        return R.style.DialogTheme
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.bottom_sheet_road_trip_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = BottomSheetRoadTripFragmentBinding.bind(view)
        binding.btnSubmit.setOnClickListener(this)

        TripModeService.roadTripLiveData.observe(this) {
            if (it?.roadTrip?.id != null){
                binding.btnSubmit.isEnabled = true
                roadTripId = it.roadTrip.id.toString()
            }else{
                binding.btnSubmit.isEnabled = false
                roadTripId = ""
            }
        }

    }

    override fun onClick(p0: View?) {

        var privateCarChecked = binding.radioPrivateCar.isChecked
        var privateBikeChecked = binding.radioBike.isChecked
        var publicBusChecked = binding.radioPublicBus.isChecked
        var publicTaxiChecked = binding.radioTaxi.isChecked

        var type = ""
        var isPublic = false
        var name = ""

        if (privateBikeChecked || privateCarChecked) {
            name = "Private Vehicle"
            type = "private"
            isPublic = false
        }else if(publicBusChecked) {
            name = "Public Bus"
            type = "public"
            isPublic = true
        }else if(publicTaxiChecked) {
            name = "Taxi"
            type = "public"
            isPublic = true
        }

        val vehicleType = VehicleType(isPublic, type)
        val vehicle = Vehicle(name, vehicleType, roadTripId)

        lifecycleScope.launch {
            vehicleRepository.createVehicle(vehicle)
        }

        vehicleRepository.vehicleResponseLiveData.observe(this) {
            if (it?.vehicle != null) {
                dismiss()
            }
        }
    }
}