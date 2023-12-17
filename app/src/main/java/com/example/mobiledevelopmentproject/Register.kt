package com.example.mobiledevelopmentproject

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class Register : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance() // Initialize firestore

        val emailEditText = findViewById<TextInputEditText>(R.id.email)
        val passwordEditText = findViewById<TextInputEditText>(R.id.password)
        val firstnameEditText = findViewById<TextInputEditText>(R.id.firstname)
        val lastnameEditText = findViewById<TextInputEditText>(R.id.lastname)
        val adressEditText = findViewById<TextInputEditText>(R.id.adress)
        val cityEditText = findViewById<TextInputEditText>(R.id.city)
        val genderSpinner = findViewById<Spinner>(R.id.spinner_gender)





        val registerButton = findViewById<Button>(R.id.btn_register)
        val goBack = findViewById<Button>(R.id.btn_goback)

        goBack.setOnClickListener{

            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
        registerButton.setOnClickListener{

            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val firstname = firstnameEditText.text.toString().trim()
            val lastname = lastnameEditText.text.toString().trim()
            val city = cityEditText.text.toString().trim()
            val adress = adressEditText.text.toString()
            val gender = genderSpinner.selectedItem.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                registerUser(email, password, firstname, lastname,adress, gender, city )
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }


        }
    }

    private fun registerUser(email: String, password: String, firstname : String, lastname : String, adress : String, gender: String, city : String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid

                    if (userId != null) {
                        saveUserData(userId, email, adress, gender, firstname, lastname, city)
                    }

                    val intent = Intent(this, Login::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    //Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    //updateUI(null)
                }
            }
    }
    private fun saveUserData(userId: String, email: String, address: String, gender: String, firstname: String, lastname: String, city: String) {
        val userMap = hashMapOf(
            "email" to email,
            "address" to address,
            "gender" to gender,
            "firstname" to firstname,
            "lastname" to lastname,
            "city" to city,
            "bestPosition" to "Links",
            "bestHand" to "Links",
            "bestTime" to "Ochtend"
            // Add more fields as needed
        )

        // Add the user information to Firestore under the "users" collection with the user ID as the document ID
        firestore.collection("users")
            .document(userId)
            .set(userMap)
            .addOnSuccessListener {
                // Success
                uploadDefaultImage(email);
                Toast.makeText(this, "User information saved successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                // Failure
                Toast.makeText(this, "Error saving user information: $e", Toast.LENGTH_SHORT).show()
            }
    }
    // Inside the uploadImage function in the Register activity
    private fun uploadDefaultImage(email: String) {
        val storageReference: StorageReference = FirebaseStorage.getInstance().reference
        val filename = "profile_picture.jpg"
        val imageRef = storageReference.child("profilepictures/$email/$filename")

        val defaultPictureRef = storageReference.child("profilepictures/DefaultProfilePicture.jpg")

        defaultPictureRef.getBytes(Long.MAX_VALUE)
            .addOnSuccessListener { bytes ->
                imageRef.putBytes(bytes)
                    .addOnSuccessListener { taskSnapshot ->
                        Toast.makeText(this, "Default image uploaded successfully", Toast.LENGTH_SHORT).show()

                        imageRef.downloadUrl.addOnSuccessListener { uri ->
                            val downloadUrl = uri.toString()
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Default image upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to download default profile picture: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }




}