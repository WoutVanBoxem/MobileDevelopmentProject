package com.example.mobiledevelopmentproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue


class MatchDetailsActivity : AppCompatActivity() {
    private lateinit var clubId: String
    private lateinit var matchId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match_details)

        val match = intent.getSerializableExtra("MATCH") as? Match ?: return
        clubId = intent.getStringExtra("CLUB_ID") ?: return
        matchId = match.id

        val clubName = intent.getStringExtra("CLUB_NAME") ?: "Onbekende Club"
        val clubAddress = intent.getStringExtra("CLUB_ADDRESS") ?: "Onbekend Adres"

        Log.d("MatchDetailsActivity", "Clubnaam: $clubName, Clubadres: $clubAddress, Club ID: $clubId, Match ID: $matchId")

        // Stel de UI-elementen in
        findViewById<TextView>(R.id.tvClubName).text = clubName
        findViewById<TextView>(R.id.tvClubAddress).text = clubAddress
        findViewById<TextView>(R.id.tvField).text = match.veldNaam
        findViewById<TextView>(R.id.tvTimeSlot).text = TimeSlotUtil.getTimeSlotFromId(match.tijdslotId.toInt())
        findViewById<TextView>(R.id.tvParticipants).text = match.deelnemers.joinToString(", ")

        // Inschrijfknop
        findViewById<Button>(R.id.btnSignUp).setOnClickListener {
            signUpForMatch(match)
        }

        // Annuleerknop
        findViewById<Button>(R.id.btnCancel).setOnClickListener {
            finish()
        }
    }

    private fun signUpForMatch(match: Match) {
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: return

        val db = FirebaseFirestore.getInstance()
        db.collection("clubs").document(clubId).collection("reservaties").document(matchId)
            .update("deelnemers", FieldValue.arrayUnion(currentUserEmail))
            .addOnSuccessListener {
                Log.d("MatchDetailsActivity", "Succesvol ingeschreven voor de wedstrijd met e-mail: $currentUserEmail")
                navigateToHome()
            }
            .addOnFailureListener { e ->
                Log.e("MatchDetailsActivity", "Fout bij inschrijving voor wedstrijd: ${e.message}")
            }
    }



    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
