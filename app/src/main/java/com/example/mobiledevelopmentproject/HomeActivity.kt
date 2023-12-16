package com.example.mobiledevelopmentproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Tasks
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeActivity : AppCompatActivity() {
    private lateinit var reservationsAdapter: HomeAdapter
    private lateinit var matchesAdapter: HomeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_screen)

        val reserveerVeldjeButton: Button = findViewById(R.id.btnReserveCourt)
        reserveerVeldjeButton.setOnClickListener {
            val intent = Intent(this, ClubsActivity::class.java)
            intent.putExtra("action","reserveField")
            startActivity(intent)
        }

        val btnFindMatch = findViewById<Button>(R.id.btnFindMatch)
        btnFindMatch.setOnClickListener{
            val intent = Intent(this, ClubsActivity::class.java)
            intent.putExtra("action","findMatch")
            startActivity(intent)
        }

        val btnViewHistory = findViewById<Button>(R.id.btnViewHistory)
        btnViewHistory.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        val rvReservations: RecyclerView = findViewById(R.id.rvYourReservations)
        reservationsAdapter = HomeAdapter(mutableListOf())
        rvReservations.adapter = reservationsAdapter
        rvReservations.layoutManager = LinearLayoutManager(this)

        val rvMatches: RecyclerView = findViewById(R.id.rvYourMatches)
        matchesAdapter = HomeAdapter(mutableListOf())
        rvMatches.adapter = matchesAdapter
        rvMatches.layoutManager = LinearLayoutManager(this)

        fetchMatchesAndReservations()

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_profile -> {
                    // Handle profile navigation
                    startActivity(Intent(this, Profile::class.java))
                    return@setOnNavigationItemSelectedListener true
                }
                else -> return@setOnNavigationItemSelectedListener false
            }
        }
    }

    private fun fetchMatchesAndReservations() {
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: return
        val db = FirebaseFirestore.getInstance()

        val today = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val formattedTodayDate = dateFormat.format(today.time)

        db.collection("clubs")
            .get()
            .addOnSuccessListener { clubDocuments ->
                val allReservations = mutableListOf<Match>()
                val allMatches = mutableListOf<Match>()
                val participantEmailsSet = mutableSetOf<String>()

                clubDocuments.forEach { clubDoc ->
                    val clubId = clubDoc.id
                    val clubName = clubDoc.getString("naam") ?: "Onbekende Club"
                    val clubAddress = "${clubDoc.getString("straat")} ${clubDoc.getString("huisnummer")}, ${clubDoc.getString("gemeente")}"

                    db.collection("clubs").document(clubId).collection("reservaties")
                        .whereArrayContains("deelnemers", currentUserEmail)
                        .get()
                        .addOnSuccessListener { reservationDocuments ->
                            reservationDocuments.forEach { reservationDoc ->
                                val match = Match(
                                    id = reservationDoc.id,
                                    datum = reservationDoc.getString("datum") ?: "",
                                    deelnemers = reservationDoc.get("deelnemers") as List<String>,
                                    isPubliek = reservationDoc.getBoolean("isPubliek") ?: false,
                                    tijdslotId = reservationDoc.getString("tijdslotId") ?: "",
                                    veldNaam = reservationDoc.getString("veldNaam") ?: "",
                                    clubNaam = clubName,
                                    clubAdres = clubAddress
                                )

                                participantEmailsSet.addAll(match.deelnemers)

                                if (match.datum >= formattedTodayDate) {
                                    if (match.isPubliek) {
                                        allMatches.add(match)
                                    } else {
                                        allReservations.add(match)
                                    }
                                }
                            }

                            fetchUserNames(participantEmailsSet.toList()) { userNamesMap ->
                                allReservations.forEach { match ->
                                    match.deelnemers = match.deelnemers.map { email ->
                                        userNamesMap[email] ?: email
                                    }
                                }

                                allMatches.forEach { match ->
                                    match.deelnemers = match.deelnemers.map { email ->
                                        userNamesMap[email] ?: email
                                    }
                                }

                                val sortedReservations = allReservations.sortedBy { it.datum }
                                val sortedMatches = allMatches.sortedBy { it.datum }

                                reservationsAdapter.updateMatches(sortedReservations)
                                matchesAdapter.updateMatches(sortedMatches)
                            }
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("HomeActivity", "Error fetching clubs", e)
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
    }



}
