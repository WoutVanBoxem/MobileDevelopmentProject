package com.example.mobiledevelopmentproject

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class HistoryActivity : AppCompatActivity() {
    private lateinit var historyAdapter: HomeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val rvHistory: RecyclerView = findViewById(R.id.rvHistory)
        historyAdapter = HomeAdapter(mutableListOf())
        rvHistory.adapter = historyAdapter
        rvHistory.layoutManager = LinearLayoutManager(this)

        fetchHistoricalMatchesAndReservations()
    }

    private fun fetchHistoricalMatchesAndReservations() {
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: return
        val db = FirebaseFirestore.getInstance()

        val today = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val formattedTodayDate = dateFormat.format(today.time)

        Log.d("HistoryActivity", "Fetching historical matches and reservations for user: $currentUserEmail")

        db.collection("clubs")
            .get()
            .addOnSuccessListener { clubDocuments ->
                val allHistoricalMatches = mutableListOf<Match>()
                val userEmailToNameMap = mutableMapOf<String, String>()

                for (clubDoc in clubDocuments) {
                    val clubId = clubDoc.id
                    val clubName = clubDoc.getString("naam") ?: "Onbekende Club"
                    val clubAddress = "${clubDoc.getString("straat")} ${clubDoc.getString("huisnummer")}, ${clubDoc.getString("gemeente")}"

                    db.collection("clubs").document(clubId).collection("reservaties")
                        .whereArrayContains("deelnemers", currentUserEmail)
                        .whereLessThan("datum", formattedTodayDate)
                        .get()
                        .addOnSuccessListener { reservationDocuments ->
                            for (reservationDoc in reservationDocuments) {
                                val match = reservationDoc.toObject(Match::class.java).apply {
                                    id = reservationDoc.id
                                    this.clubNaam = clubName
                                    this.clubAdres = clubAddress
                                }
                                allHistoricalMatches.add(match)
                                match.deelnemers.forEach { userEmailToNameMap[it] = "" } // Gebruik een lege string in plaats van null

                                Log.d("HistoryActivity", "Added historical match: $match")
                            }

                            fetchUserNames(userEmailToNameMap.keys.toList()) { userNamesMap ->
                                allHistoricalMatches.forEach { match ->
                                    match.deelnemers = match.deelnemers.map { email ->
                                        userNamesMap[email] ?: email
                                    }

                                    Log.d("HistoryActivity", "Updated match with user names: $match")
                                }

                                val sortedHistoricalMatches = allHistoricalMatches.sortedByDescending { it.datum }
                                historyAdapter.updateMatches(sortedHistoricalMatches)
                            }
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("HistoryActivity", "Error fetching clubs", e)
            }
    }

    fun fetchUserNames(emails: List<String>, onComplete: (Map<String, String>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val userNamesMap = mutableMapOf<String, String>()

        val tasks = emails.map { email ->
            db.collection("users").whereEqualTo("email", email).get().addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                } else {
                    for (document in documents) {
                        val user = document.toObject(UserClass::class.java)
                        userNamesMap[email] = "${user.firstname} ${user.lastname}"
                    }
                }
            }.addOnFailureListener { e ->
                Log.e("HomeActivity", "Error bij ophalen van gebruikersnaam voor e-mail $email", e)
            }
        }

        Tasks.whenAllSuccess<DocumentSnapshot>(tasks).addOnSuccessListener {
            onComplete(userNamesMap)
        }.addOnFailureListener { e ->
            Log.e("HomeActivity", "Error bij het voltooien van alle gebruikerstaken", e)
        }
    }}

