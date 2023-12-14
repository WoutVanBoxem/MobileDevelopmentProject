package com.example.mobiledevelopmentproject

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class MatchAdapter(
    private val matches: List<Match>,
    private val clubName: String,
    private val clubAddress: String,
    private val clubId: String
) : RecyclerView.Adapter<MatchAdapter.MatchViewHolder>() {

    class MatchViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMatchDate: TextView = view.findViewById(R.id.tvMatchDate)
        val tvMatchParticipants: TextView = view.findViewById(R.id.tvMatchParticipants)
        val tvMatchTimeSlot: TextView = view.findViewById(R.id.tvMatchTimeSlot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.match_item, parent, false)
        return MatchViewHolder(view)
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        val match = matches[position]
        holder.tvMatchDate.text = "Datum: ${formatDateToEuropean(match.datum)}"
        holder.tvMatchParticipants.text = "Deelnemers: ${match.deelnemers.joinToString(", ")}"
        holder.tvMatchTimeSlot.text = "Tijdslot: ${TimeSlotUtil.getTimeSlotFromId(match.tijdslotId.toInt())}"

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, MatchDetailsActivity::class.java).apply {
                putExtra("MATCH", match)
                putExtra("CLUB_NAME", clubName)
                putExtra("CLUB_ADDRESS", clubAddress)
                putExtra("CLUB_ID", clubId)
            }
            context.startActivity(intent)
        }
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
}
