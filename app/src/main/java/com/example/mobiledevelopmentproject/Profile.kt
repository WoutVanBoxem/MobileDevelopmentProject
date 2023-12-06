package com.example.mobiledevelopmentproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseUser
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView

class Profile : AppCompatActivity() {



    private lateinit var auth: FirebaseAuth

    companion object {
        private const val TAG = "ProfileActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)


        auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val currentUser: FirebaseUser? = auth.currentUser

        if (currentUser?.email != null) {
            db.collection("users")
                .whereEqualTo("email", currentUser.email)
                .limit(1)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        // Access the document data here
                        val user = documents.documents[0].toObject(UserClass::class.java)
                        Log.d(TAG, "User object: $user")

                        findViewById<TextView>(R.id.emailTextView).text = user?.email
                        findViewById<TextView>(R.id.addressTextView).text = user?.address
                        findViewById<TextView>(R.id.firstNameTextView).text = user?.firstname
                        findViewById<TextView>(R.id.lastNameTextView).text = user?.lastname
                        findViewById<TextView>(R.id.cityTextView).text = user?.city
                        findViewById<TextView>(R.id.genderTextView).text = user?.gender

                        // Do something with the user object
                    } else {
                        // Document with the specified email not found
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle errors
                    //Log.d(TAG, "Error getting documents: ", exception)
                }
        }

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Handle profile navigation
                    startActivity(Intent(this, HomeActivity::class.java))
                    return@setOnNavigationItemSelectedListener true
                }
                else -> return@setOnNavigationItemSelectedListener false
            }
        }


    }
}