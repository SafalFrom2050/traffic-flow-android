package com.example.trafficflow.falsereport.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trafficflow.R
import com.example.trafficflow.falsereport.Model.FalseReport

class FalseReportsAdapter(private val falseReportsList: List<FalseReport>):
    RecyclerView.Adapter<FalseReportsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.rv_item_false_report, parent, false)

        return FalseReportsAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (falseReportsList[position].details == null || falseReportsList[position].details == ""){
            holder.txtDetails.text = "No details"
        }else{
            holder.txtDetails.text = falseReportsList[position].details
        }

        holder.txtReporter.text = "By ${falseReportsList[position].user?.fname} ${falseReportsList[position].user?.lname}"
    }

    override fun getItemCount(): Int {
        return falseReportsList.size
    }



    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val txtDetails = view.findViewById<TextView>(R.id.details)
        val txtReporter = view.findViewById<TextView>(R.id.reporter)
    }

}