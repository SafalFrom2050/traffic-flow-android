package com.example.trafficflow.ui.achievements.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trafficflow.R
import com.example.trafficflow.ui.achievements.Model.Achievement

class AchievementsAdapter(private val achievementList: List<Achievement>):
    RecyclerView.Adapter<AchievementsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.rv_item_achievement, parent, false)

        return AchievementsAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.labelName.text = achievementList[position].name
        holder.labelLevel.text = achievementList[position].level.toString()
        holder.labelRequiredPoints.text = achievementList[position].pointsRequired.toString()
    }

    override fun getItemCount(): Int {
        return achievementList.size
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val labelName = view.findViewById<TextView>(R.id.labelName)
        val labelRequiredPoints = view.findViewById<TextView>(R.id.labelRequiredPoints)
        val labelLevel = view.findViewById<TextView>(R.id.labelLevel)
    }
}