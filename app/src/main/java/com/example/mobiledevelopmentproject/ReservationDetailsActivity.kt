package com.example.mobiledevelopmentproject

import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class ReservationDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation_details)

        // Ontvang de data van de intent
        val field = intent.getSerializableExtra("FIELD") as Field
        val date = intent.getStringExtra("DATE")
        val timeSlot = intent.getStringExtra("TIMESLOT")

        // Stel de velden in de UI in
        findViewById<TextView>(R.id.fieldNameTextView).text = field.naam
        findViewById<TextView>(R.id.fieldTypeTextView).text = field.type
        findViewById<TextView>(R.id.dateTextView).text = date
        findViewById<TextView>(R.id.timeSlotTextView).text = timeSlot

        // Toggle voor publieke wedstrijd
        val publicMatchToggle = findViewById<Switch>(R.id.publicMatchSwitch)

        // Voeg een click listener toe aan de bevestigingsknop
        findViewById<Button>(R.id.confirmButton).setOnClickListener {
            saveReservation(field, date, timeSlot, publicMatchToggle.isChecked)
        }
    }

    private fun saveReservation(field: Field, date: String?, timeSlot: String?, isPublic: Boolean) {
        val reservation = hashMapOf(
            "datum" to date,
            "tijdslotId" to timeSlot,
            "veldNaam" to field.naam,
            "isPubliek" to isPublic,
            "deelnemers" to listOf(/* Gebruikersnaam of ID van de gebruiker */)
        )

        val db = FirebaseFirestore.getInstance()
        db.collection("clubs").document(clubId).collection("reservaties")
            .add(reservation)
            .addOnSuccessListener {
                // Bevestig succesvolle opslag
            }
            .addOnFailureListener {
                // Behandel de fout
            }
    }

}
