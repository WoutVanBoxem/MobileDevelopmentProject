package com.example.mobiledevelopmentproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class MatchSearchActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MatchAdapter // Pas dit aan met je MatchAdapter
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match_search)

        val clubId = intent.getStringExtra("CLUB_ID") ?: return

        recyclerView = findViewById(R.id.rvMatches)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchMatches(clubId)
    }

    private fun fetchMatches(clubId: String) {
        db.collection("clubs").document(clubId).collection("reservaties")
            .whereEqualTo("isPubliek", true)
            .get()
            .addOnSuccessListener { documents ->
                val matchesList = ArrayList<Match>()
                for (document in documents) {
                    val match = document.toObject(Match::class.java)
                    matchesList.add(match)
                }
                adapter = MatchAdapter(matchesList)
                recyclerView.adapter = adapter
            }
            .addOnFailureListener { exception ->
            }
    }

}
