package com.example.trafficflow.ui.bottomsheets.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trafficflow.R
import com.example.trafficflow.RetrofitInstance
import com.example.trafficflow.ui.bottomsheets.Model.IncidentType

class IncidentTypesAdapter(private val incidentTypeList: List<IncidentType>) :
    RecyclerView.Adapter<IncidentTypesAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.rv_item_incident_type, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nameIncident.text = incidentTypeList[position].name

        val y = Glide.with(holder.itemView.context)
            .load(RetrofitInstance.BASE_URL+incidentTypeList[position].image)
            .into(holder.imageIncident)
    }

    override fun getItemCount(): Int {
        return incidentTypeList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageIncident = itemView.findViewById<ImageView>(R.id.imageIncident)
        val nameIncident = itemView.findViewById<TextView>(R.id.nameIncident)
    }
}