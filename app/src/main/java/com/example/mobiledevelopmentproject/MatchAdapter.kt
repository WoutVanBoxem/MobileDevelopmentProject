package com.example.mobiledevelopmentproject

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class MatchAdapter(private val matches: List<Match>) : RecyclerView.Adapter<MatchAdapter.MatchViewHolder>() {

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
        val deelnemersString = match.deelnemers.joinToString(", ")
        holder.tvMatchParticipants.text = "Deelnemers: $deelnemersString"
        val readableTimeSlot = TimeSlotUtil.getTimeSlotFromId(match.tijdslotId.toIntOrNull() ?: -1)
        holder.tvMatchTimeSlot.text = "Tijdslot: $readableTimeSlot"
    }

    override fun getItemCount() = matches.size

    private fun formatDateToEuropean(dateString: String?): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
            val date = inputFormat.parse(dateString)
            outputFormat.format(date)
        } catch (e: Exception) {
            Log.e("MatchSearchActivity", "Error formatting date", e)
            dateString ?: ""
        }
    }
    fun fetchUserNames(emails: List<String>, onComplete: (Map<String, String>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val userNamesMap = mutableMapOf<String, String>()

        val tasks = emails.map { email ->
            db.collection("Users").whereEqualTo("email", email).get().addOnSuccessListener { documents ->
                for (document in documents) {
                    val user = document.toObject(UserClass::class.java)
                    userNamesMap[email] = "${user.firstname} ${user.lastname}"
                }
            }
        }

        Tasks.whenAllSuccess<DocumentSnapshot>(tasks).addOnSuccessListener {
            onComplete(userNamesMap)
        }
    }
}
