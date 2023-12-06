package com.example.mobiledevelopmentproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class ReservationDetailsActivity : AppCompatActivity() {
    private lateinit var clubId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation_details)


        val field = intent.getSerializableExtra("FIELD") as Field
        val date = intent.getStringExtra("DATE")
        val formattedDate = formatDateToEuropean(date)
        val timeSlot = intent.getStringExtra("TIMESLOT")
        val timeslotId = intent.getStringExtra("TIMESLOTID")
        val clubName = intent.getStringExtra("CLUB_NAME")
        val clubAddress = intent.getStringExtra("CLUB_ADDRESS")
        clubId = intent.getStringExtra("CLUB_ID") ?: return



        findViewById<TextView>(R.id.clubNameTextView).text = clubName
        findViewById<TextView>(R.id.clubAddressTextView).text = clubAddress
        findViewById<TextView>(R.id.fieldNameTextView).text = field.naam
        findViewById<TextView>(R.id.fieldTypeTextView).text = field.type
        findViewById<TextView>(R.id.dateTextView).text = formattedDate
        findViewById<TextView>(R.id.timeSlotTextView).text = timeSlot


        val publicMatchToggle = findViewById<Switch>(R.id.publicMatchSwitch)


        findViewById<Button>(R.id.confirmButton).setOnClickListener {
            saveReservation(field, date, timeslotId, publicMatchToggle.isChecked)
        }
    }

    private fun saveReservation(field: Field, date: String?, timeSlot: String?, isPublic: Boolean) {
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email

        val participants = if (currentUserEmail != null) {
            listOf(currentUserEmail)
        } else {
            listOf<String>()
        }

        val reservation = hashMapOf(
            "datum" to date,
            "tijdslotId" to timeSlot,
            "veldNaam" to field.naam,
            "isPubliek" to isPublic,
            "deelnemers" to participants
        )

        val db = FirebaseFirestore.getInstance()
        db.collection("clubs").document(clubId).collection("reservaties")
            .add(reservation)
            .addOnSuccessListener {
                navigateToHome()
            }
            .addOnFailureListener {

            }
    }
    private fun formatDateToEuropean(dateString: String?): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
            val date = inputFormat.parse(dateString)
            outputFormat.format(date)
        } catch (e: Exception) {
            Log.e("ReservationDetailsActivity", "Error formatting date", e)
            dateString ?: ""
        }
    }
    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

}
