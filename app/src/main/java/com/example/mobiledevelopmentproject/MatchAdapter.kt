package com.example.mobiledevelopmentproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MatchAdapter(private val matches: List<Match>) : RecyclerView.Adapter<MatchAdapter.MatchViewHolder>() {

    class MatchViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMatchDate: TextView = view.findViewById(R.id.tvMatchDate)
        val tvMatchParticipants: TextView = view.findViewById(R.id.tvMatchParticipants)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.match_item, parent, false)
        return MatchViewHolder(view)
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        val match = matches[position]
        holder.tvMatchDate.text = "Datum: ${match.datum}"
        val deelnemersString = match.deelnemers.joinToString(", ")
        holder.tvMatchParticipants.text = "Deelnemers: $deelnemersString"
    }

    override fun getItemCount() = matches.size
}
