package com.example.mobiledevelopmentproject

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

class Profile : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var imageView: ImageView
    private var userEmail: String = ""

    companion object {
        private const val TAG = "ProfileActivity"
        private const val PICK_IMAGE_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val enableEditTextButton = findViewById<Button>(R.id.enableEditTextButton)
        val saveEditTextButton = findViewById<Button>(R.id.saveEditTextButton)
        val uploadButton = findViewById<Button>(R.id.uploadButton)
        imageView = findViewById<ImageView>(R.id.imageView)

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

                        userEmail = user?.email.toString();
                        loadProfilePicture(user?.email.toString())

                        val genderSpinner = findViewById<Spinner>(R.id.spinner_gender)
                        val genderArray = resources.getStringArray(R.array.gender_options)
                        val selectedGenderIndex = genderArray.indexOf(user?.gender)
                        if (selectedGenderIndex != -1) {
                            genderSpinner.setSelection(selectedGenderIndex)
                        } else {
                            // Handle the case where the user's gender is not found in the array
                            Log.e(TAG, "User gender not found in gender options array")
                        }

                        val bestHandSpinner = findViewById<Spinner>(R.id.spinner_bestHand)
                        val bestHandArray = resources.getStringArray(R.array.best_hand)
                        val selectedBestHandIndex = bestHandArray.indexOf(user?.bestHand)
                        if (selectedBestHandIndex != -1) {
                            bestHandSpinner.setSelection(selectedBestHandIndex)
                        } else {
                            // Handle the case where the user's gender is not found in the array
                            Log.e(TAG, "User best hand not found in best hand options array")
                        }

                        val bestCourtPositionSpinner = findViewById<Spinner>(R.id.spinner_courtPosition)
                        val bestCourtPositionArray = resources.getStringArray(R.array.court_position)
                        val selectedCourtPositionIndex = bestCourtPositionArray.indexOf(user?.bestPosition)
                        if (selectedCourtPositionIndex != -1) {
                            bestCourtPositionSpinner.setSelection(selectedCourtPositionIndex)
                        } else {
                            // Handle the case where the user's gender is not found in the array
                            Log.e(TAG, "User best hand not found in best hand options array")
                        }

                        val bestTimeSpinner = findViewById<Spinner>(R.id.spinner_bestTime)
                        val bestTimeArray = resources.getStringArray(R.array.best_time)
                        val selectedTimeIndex = bestTimeArray.indexOf(user?.bestTime)
                        if (selectedTimeIndex != -1) {
                            bestTimeSpinner.setSelection(selectedTimeIndex)
                        } else {
                            // Handle the case where the user's gender is not found in the array
                            Log.e(TAG, "User best hand not found in best hand options array")
                        }

                        uploadButton.setOnClickListener {
                            // Obtain the imageUri using Intent.ACTION_PICK or any other method
                            val intent = Intent(Intent.ACTION_PICK)
                            intent.type = "image/*"
                            startActivityForResult(intent, PICK_IMAGE_REQUEST)
                        }

                    } else {
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle exceptions during data retrieval
                    Log.e(TAG, "Error getting documents: ", exception)
                }
        }

        val editEmail = findViewById<TextView>(R.id.emailTextView)
        val editAddress = findViewById<TextView>(R.id.addressTextView)
        val editFirstName = findViewById<TextView>(R.id.firstNameTextView)
        val editLastName = findViewById<TextView>(R.id.lastNameTextView)
        val editCityName = findViewById<TextView>(R.id.cityTextView)
        val editGender = findViewById<Spinner>(R.id.spinner_gender)
        val editBestHand = findViewById<Spinner>(R.id.spinner_bestHand)
        val editBestCourtPosition = findViewById<Spinner>(R.id.spinner_courtPosition)
        val editBestTime = findViewById<Spinner>(R.id.spinner_bestTime)
        enableEditTextButton.setOnClickListener {
            // Enable the EditText elements
            editAddress.isEnabled = true
            editFirstName.isEnabled = true
            editLastName.isEnabled = true
            editCityName.isEnabled = true
            editGender.isEnabled = true
        }

        saveEditTextButton.setOnClickListener {
            val collectionRef = db.collection("users")
            val userEmail = editEmail.text.toString()

            val updates = mapOf(
                "address" to editAddress.text.toString(),
                "firstname" to editFirstName.text.toString(),
                "city" to editCityName.text.toString(),
                "lastname" to editLastName.text.toString(),
                "gender" to editGender.selectedItem.toString(),
                "bestHand" to editBestHand.selectedItem.toString(),
                "bestPosition" to editBestCourtPosition.selectedItem.toString(),
                "bestTime" to editBestTime.selectedItem.toString()
                // Add other fields as needed
            )

            collectionRef.whereEqualTo("email", userEmail).limit(1)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val documentId = documents.documents[0].id

                        // Update the document with the new values
                        collectionRef.document(documentId).update(updates)
                            .addOnSuccessListener {
                                // Document successfully updated
                                Log.d(TAG, "DocumentSnapshot successfully updated!")
                                finish()
                                startActivity(intent)
                            }
                            .addOnFailureListener { e ->
                                // Handle errors
                                Log.w(TAG, "Error updating document", e)
                            }
                    } else {
                        // Document with the specified email not found
                        Log.e(TAG, "Document with email $userEmail not found")
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle errors
                    Log.w(TAG, "Error getting documents: ", exception)
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

    // Override onActivityResult to handle the result of image selection
    private fun loadProfilePicture(email: String?) {
        if (email != null) {
            // Initialize storage reference
            val storageReference: StorageReference = FirebaseStorage.getInstance().reference

            val imageRef = storageReference.child("profilepictures/$email/profile_picture.jpg")

            imageRef.downloadUrl.addOnSuccessListener { uri ->
                val downloadUrl = uri.toString()

                // Load the image into the imageView using Glide
                Glide.with(this)
                    .load(downloadUrl)
                    .override(400, 400)
                    .fitCenter()
                    .into(imageView)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            val imageUri: Uri = data.data!!

            uploadImage(imageUri,userEmail)
        }
    }

    private fun uploadImage(imageUri: Uri, email: String) {
        val storageReference: StorageReference = FirebaseStorage.getInstance().reference

        val filename = "profile_picture.jpg"

        val existingImageRef = storageReference.child("profilepictures/$email/$filename")

        // Logging for debugging
        Log.d("UploadImage", "Email: $email")

        // Check if the file exists before attempting to delete
        existingImageRef.metadata
            .addOnSuccessListener { metadata ->
                // File exists, proceed with deletion
                existingImageRef.delete()
                    .addOnSuccessListener {
                        // Deletion successful, proceed with upload
                        val newImageRef = storageReference.child("profilepictures/$email/$filename")

                        newImageRef.putFile(imageUri)
                            .addOnSuccessListener { taskSnapshot ->
                                // Image uploaded successfully
                                Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show()

                                // You can also get the download URL here
                                newImageRef.downloadUrl.addOnSuccessListener { uri ->
                                    val downloadUrl = uri.toString()
                                    // Now you can use the download URL as needed
                                }
                            }
                            .addOnFailureListener { e ->
                                // Image upload failed
                                Toast.makeText(this, "Image upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .addOnFailureListener { e ->
                        // Existing image deletion failed
                        Toast.makeText(this, "Existing image deletion failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                // File does not exist, proceed with upload
                val newImageRef = storageReference.child("profilepictures/$email/$filename")

                // Logging for debugging
                Log.d("UploadImage", "New Image Path: ${newImageRef.path}")

                newImageRef.putFile(imageUri)
                    .addOnSuccessListener { taskSnapshot ->
                        // Image uploaded successfully
                        Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show()

                        // You can also get the download URL here
                        newImageRef.downloadUrl.addOnSuccessListener { uri ->
                            val downloadUrl = uri.toString()
                            // Now you can use the download URL as needed
                        }
                    }
                    .addOnFailureListener { e ->
                        // Image upload failed
                        Toast.makeText(this, "Image upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
    }







}
