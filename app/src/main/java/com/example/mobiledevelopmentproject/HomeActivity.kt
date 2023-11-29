package com.example.mobiledevelopmentproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

// In je Home Activity
class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_screen)

        val reserveerVeldjeButton: Button = findViewById(R.id.btnReserveCourt)
        reserveerVeldjeButton.setOnClickListener {
            val intent = Intent(this, ClubsActivity::class.java)
            startActivity(intent)
        }
    }
}
