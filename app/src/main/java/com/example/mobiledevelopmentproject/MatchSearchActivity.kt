package com.example.mobiledevelopmentproject

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MatchSearchActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MatchAdapter
    private val db = FirebaseFirestore.getInstance()
    private lateinit var clubName: String
    private lateinit var clubAddress: String
    private lateinit var clubId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match_search)

        clubId = intent.getStringExtra("CLUB_ID") ?: return
        clubName = intent.getStringExtra("CLUB_NAME") ?: "Onbekende Club"
        clubAddress = intent.getStringExtra("CLUB_ADDRESS") ?: "Onbekend Adres"
        val tvClubName = findViewById<TextView>(R.id.tvClubName)
        tvClubName.text = clubName

        recyclerView = findViewById(R.id.rvMatches)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchMatches(clubId)
    }
    fun fetchUserNames(emails: List<String>, onComplete: (Map<String, String>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val userNamesMap = mutableMapOf<String, String>()

        // Loggen van de e-mailadressen die worden opgehaald
        Log.d("MatchSearchActivity", "Ophalen van namen voor e-mails: $emails")

        val tasks = emails.map { email ->
            db.collection("users").whereEqualTo("email", email).get().addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Log.d("MatchSearchActivity", "Geen gebruiker gevonden voor e-mail: $email")
                } else {
                    for (document in documents) {
                        val user = document.toObject(UserClass::class.java)
                        userNamesMap[email] = "${user.firstname} ${user.lastname}"
                        Log.d("MatchSearchActivity", "Gebruikersnaam gevonden: ${user.firstname} ${user.lastname} voor e-mail: $email")
                    }
                }
            }.addOnFailureListener { e ->
                Log.e("MatchSearchActivity", "Error bij ophalen van gebruikersnaam voor e-mail $email", e)
            }
        }

        Tasks.whenAllSuccess<DocumentSnapshot>(tasks).addOnSuccessListener {
            Log.d("MatchSearchActivity", "Alle gebruikersnamen opgehaald: $userNamesMap")
            onComplete(userNamesMap)
        }.addOnFailureListener { e ->
            Log.e("MatchSearchActivity", "Error bij het voltooien van alle gebruikerstaken", e)
        }
    }


    private fun fetchMatches(clubId: String) {
        val today = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val formattedTodayDate = dateFormat.format(today.time)
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        val currentTimeslotId = TimeSlotUtil.getCurrentTimeSlotId()

        db.collection("clubs").document(clubId).collection("reservaties")
            .whereEqualTo("isPubliek", true)
            .get()
            .addOnSuccessListener { documents ->
                val matchesList = ArrayList<Match>()
                for (document in documents) {
                    val match = document.toObject(Match::class.java).apply {
                        id = document.id // Stel het document ID in
                    }

                    if (match.datum >= formattedTodayDate &&
                        match.deelnemers.size < 4 &&
                        (currentUserEmail !in match.deelnemers)) {

                        if (match.datum == formattedTodayDate) {
                            val matchTimeSlotId = match.tijdslotId.toIntOrNull() ?: -1
                            if (matchTimeSlotId > currentTimeslotId+1) {
                                matchesList.add(match)
                            }
                        } else {
                            matchesList.add(match)
                        }
                    }
                }

                val participantEmails = matchesList.flatMap { it.deelnemers }.distinct()
                fetchUserNames(participantEmails) { userNamesMap ->
                    matchesList.forEach { match ->
                        match.deelnemers = match.deelnemers.map { email ->
                            userNamesMap[email] ?: "Onbekende Gebruiker"
                        }
                    }
                    adapter = MatchAdapter(matchesList, clubName, clubAddress, clubId)
                    recyclerView.adapter = adapter
                }
            }
            .addOnFailureListener { exception ->
            }
    }





}
