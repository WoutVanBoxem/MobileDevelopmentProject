package com.example.mobiledevelopmentproject

import android.content.Intent
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
import java.io.Serializable
import com.example.mobiledevelopmentproject.Field
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FieldSelectionActivity : AppCompatActivity() {
    private lateinit var clubId: String
    private lateinit var clubName: String
    private lateinit var clubAddress: String
    private lateinit var timeSlots: Array<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_field_selection)
        clubId = intent.getStringExtra("CLUB_ID") ?: return
        clubName = intent.getStringExtra("CLUB_NAME") ?: return
        clubAddress = intent.getStringExtra("CLUB_ADDRESS") ?: return

        val datePicker = findViewById<DatePicker>(R.id.datepicker)
        val calendar = Calendar.getInstance()
        datePicker.minDate = calendar.timeInMillis

        val spinnerTimeSlot = findViewById<Spinner>(R.id.spinnerTimeSlot)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewAvailableFields)

        timeSlots = generateTimeSlots()
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
        var hour = 7
        var minute = 30

        while (true) {
            val start = String.format("%02d:%02d", hour, minute)
            hour += 1
            minute += 30

            if (minute >= 60) {
                hour += 1
                minute -= 60
            }

            if (hour >= 24) {
                hour -= 24 // Terug naar 0 uur
            }

            val end = String.format("%02d:%02d", hour, minute)
            slots.add("$start - $end")

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
                            Log.d("FieldSelectionActivity", "Selected field: ${selectedField.naam}")
                            Log.d("FieldSelectionActivity", "Date: $formattedDate")
                            Log.d("FieldSelectionActivity", "TimeSlot: ${timeSlots[timeSlotId]}")
                            Log.d("FieldSelectionActivity", "Club Name: $clubName")
                            Log.d("FieldSelectionActivity", "Club Address: $clubAddress")
                            Log.d("FieldSelectionActivity", "TimeSlotID : ${timeSlotId}")

                            val intent = Intent(this, ReservationDetailsActivity::class.java)
                            intent.putExtra("FIELD", selectedField as Serializable)
                            intent.putExtra("DATE", formattedDate)
                            intent.putExtra("TIMESLOT", timeSlots[timeSlotId])
                            intent.putExtra("CLUB_NAME", clubName)
                            intent.putExtra("CLUB_ADDRESS", clubAddress)
                            intent.putExtra("CLUB_ID", clubId )
                            intent.putExtra("TIMESLOTID", timeSlotId.toString())

                            startActivity(intent)
                        }
                    }
            }
            .addOnFailureListener { exception ->
                // Log of toon fout
            }
    }



}
