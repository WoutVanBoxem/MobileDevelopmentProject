package com.example.mobiledevelopmentproject

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.reflect.Field
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FieldSelectionActivity : AppCompatActivity() {
    private lateinit var clubId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_field_selection)
        clubId = intent.getStringExtra("CLUB_ID") ?: return

        val datePicker = findViewById<DatePicker>(R.id.datepicker)
        val calendar = Calendar.getInstance()
        datePicker.minDate = calendar.timeInMillis

        val spinnerTimeSlot = findViewById<Spinner>(R.id.spinnerTimeSlot)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewAvailableFields)


        // Tijdsloten instellen
        val timeSlots = generateTimeSlots()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, timeSlots)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTimeSlot.adapter = adapter

        spinnerTimeSlot.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedTimeSlotId = getTimeSlotIdForPosition(position)
                fetchAvailableFields(datePicker, selectedTimeSlotId)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)) { view, year, monthOfYear, dayOfMonth ->
            val selectedTimeSlotId = getTimeSlotIdForPosition(spinnerTimeSlot.selectedItemPosition)
            fetchAvailableFields(datePicker, selectedTimeSlotId)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun generateTimeSlots(): Array<String> {
        val slots = mutableListOf<String>()
        var hour = 7 // Beginnen bij 7 uur
        var minute = 30 // Beginnen bij 30 minuten

        while (true) { // Verander naar een oneindige lus
            val start = String.format("%02d:%02d", hour, minute)
            hour += 1 // Voeg een uur toe
            minute += 30 // Voeg 30 minuten toe

            // Correctie voor de overloop van minuten
            if (minute >= 60) {
                hour += 1
                minute -= 60
            }

            // Als we de uren moeten corrigeren omdat ze 24 overschrijden
            if (hour >= 24) {
                hour -= 24 // Terug naar 0 uur
            }

            val end = String.format("%02d:%02d", hour, minute)
            slots.add("$start - $end")

            // Stopconditie: als we net het tijdslot van 22:30 - 00:00 hebben toegevoegd
            if (hour == 0 && minute == 0) {
                break
            }
        }

        return slots.toTypedArray()
    }





    private fun getTimeSlotIdForPosition(position: Int): Int {
        return position
    }

    private fun fetchAvailableFields(datePicker: DatePicker, timeSlotId: Int) {
        val selectedDate = Calendar.getInstance().apply {
            set(datePicker.year, datePicker.month, datePicker.dayOfMonth)
        }.time

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = dateFormat.format(selectedDate)


        val db = FirebaseFirestore.getInstance()

        // Eerst alle velden ophalen
        db.collection("clubs").document(clubId).collection("velden")
            .get()
            .addOnSuccessListener { fieldDocuments ->
                val allFields = fieldDocuments.mapNotNull { document ->
                    document.id?.let { Field(it, document.getString("type") ?: "") }
                }

                // Vervolgens de gereserveerde velden ophalen
                db.collection("clubs").document(clubId).collection("reservaties")
                    .whereEqualTo("datum", formattedDate)
                    .whereEqualTo("tijdslotId", timeSlotId.toString())
                    .get()
                    .addOnSuccessListener { reservations ->
                        val reservedFieldNames = reservations.mapNotNull { it.getString("veldNaam") } // Aangepast naar het gebruik van veldnaam
                        val availableFields = allFields.filterNot { field -> reservedFieldNames.contains(field.naam) }

                        // Update de RecyclerView met de beschikbare veldjes
                        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewAvailableFields)
                        recyclerView.adapter = FieldAdapter(availableFields) { selectedField ->
                            // Start de activity of toon het dialogfragment met reserveringsdetails
                            showReservationDetails(selectedField, formattedDate, selectedTimeSlot)
                        }
                    }
            }
            .addOnFailureListener { exception ->
                // Log of toon fout
            }
    }



}
