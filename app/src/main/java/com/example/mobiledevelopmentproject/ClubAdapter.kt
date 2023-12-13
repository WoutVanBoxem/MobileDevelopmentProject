package com.example.mobiledevelopmentproject

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ClubAdapter(private val clubs: List<Club>, private val action: String) : RecyclerView.Adapter<ClubAdapter.ClubViewHolder>() {

    class ClubViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvClubName: TextView = view.findViewById(R.id.tvClubName)
        val tvClubAddress: TextView = view.findViewById(R.id.tvClubAddress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClubViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.club_item, parent, false)
        return ClubViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClubViewHolder, position: Int) {
        val club = clubs[position]
        holder.tvClubName.text = club.naam
        holder.tvClubAddress.text = "${club.straat} ${club.huisnummer}, ${club.gemeente}"

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = when (action) {
                "findMatch" -> Intent(context, MatchSearchActivity::class.java) // Pas aan naar je MatchSearchActivity
                "reserveField" -> Intent(context, FieldSelectionActivity::class.java) // Pas aan naar je FieldSelectionActivity
                else -> throw IllegalStateException("Onbekende actie")
            }
            intent.putExtra("CLUB_ID", club.id)
            intent.putExtra("CLUB_NAME", club.naam)
            intent.putExtra("CLUB_ADDRESS", club.straat + " " + club.huisnummer + " " + club.gemeente)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = clubs.size
}
