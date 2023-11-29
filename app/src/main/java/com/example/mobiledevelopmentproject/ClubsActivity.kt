package com.example.mobiledevelopmentproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobiledevelopmentproject.R
import com.example.mobiledevelopmentproject.ClubAdapter
import com.google.firebase.firestore.FirebaseFirestore


class ClubsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ClubAdapter
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_club)

        recyclerView = findViewById(R.id.rvClubs)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchClubs()
    }
    private fun fetchClubs() {
        db.collection("clubs")
            .get()
            .addOnSuccessListener { documents ->
                val clubsList = ArrayList<Club>()
                for (document in documents) {
                    val club = document.toObject(Club::class.java).copy(id = document.id)
                    clubsList.add(club)
                }
                adapter = ClubAdapter(clubsList)
                recyclerView.adapter = adapter
            }
            .addOnFailureListener { exception ->
                // Log de fout of toon een bericht aan de gebruiker
            }
    }

}