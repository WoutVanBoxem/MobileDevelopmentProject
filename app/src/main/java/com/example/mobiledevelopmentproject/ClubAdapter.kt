package com.example.mobiledevelopmentproject

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobiledevelopmentproject.Club
import com.example.mobiledevelopmentproject.R

class ClubAdapter(private val clubs: List<Club>) : RecyclerView.Adapter<ClubAdapter.ClubViewHolder>() {

    class ClubViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvClubName: TextView = view.findViewById(R.id.tvClubName)
        val tvClubAddress: TextView = itemView.findViewById(R.id.tvClubAddress)
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
            val intent = Intent(context, FieldSelectionActivity::class.java).apply {
                putExtra("CLUB_ID", club.id)
                putExtra("CLUB_NAME", club.naam)
                putExtra("CLUB_ADDRESS", club.straat + " " + club.huisnummer + " " + club.gemeente) // Voeg deze toe
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = clubs.size
}