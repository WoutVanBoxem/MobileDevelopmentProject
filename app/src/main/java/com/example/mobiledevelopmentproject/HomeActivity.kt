package com.example.mobiledevelopmentproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

// In je Home Activity
class HomeActivity : AppCompatActivity() {

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
}
