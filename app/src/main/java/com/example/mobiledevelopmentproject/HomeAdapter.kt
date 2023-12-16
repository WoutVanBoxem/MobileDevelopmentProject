package com.example.mobiledevelopmentproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class HomeAdapter(private var matches: MutableList<Match>) : RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMatchDate: TextView = view.findViewById(R.id.tvMatchDate)
        val tvMatchParticipants: TextView = view.findViewById(R.id.tvMatchParticipants)
        val tvMatchTimeSlot: TextView = view.findViewById(R.id.tvMatchTimeSlot)
        val tvClubName: TextView = view.findViewById(R.id.tvClubName)
        val tvClubAddress: TextView = view.findViewById(R.id.tvClubAddress)
        val tvVeldNaam: TextView = view.findViewById(R.id.tvVeldnaam)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.home_item, parent, false)
        return HomeViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val match = matches[position]
        holder.tvMatchDate.text = "Datum: ${formatDateToEuropean(match.datum)}"
        holder.tvMatchParticipants.text = "Deelnemers: ${match.deelnemers.joinToString(", ")}"
        holder.tvMatchTimeSlot.text = "Tijdslot: ${TimeSlotUtil.getTimeSlotFromId(match.tijdslotId.toInt())}"
        holder.tvClubName.text = match.clubNaam
        holder.tvVeldNaam.text = match.veldNaam
        holder.tvClubAddress.text = match.clubAdres

    }

    override fun getItemCount() = matches.size

    private fun formatDateToEuropean(dateString: String?): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
        return dateString?.let {
            val date = inputFormat.parse(it)
            outputFormat.format(date ?: "")
        } ?: ""
    }
    fun addMatch(match: Match) {
        matches.add(match)
    }

    fun updateMatches(newMatches: List<Match>) {
        matches.clear()
        matches.addAll(newMatches)
        notifyDataSetChanged()
    }
}