package com.example.uberride.utils

import com.google.firebase.firestore.FirebaseFirestore

class FirebaseUtils {

    private val db = FirebaseFirestore.getInstance()

    //Add a ride request to the firestore collection
    fun addRideRequest(driverId: String, onSuccess: (String) -> Unit, onFailed: (String) -> Unit) {
        db.collection("Ride Requests")
            .document(driverId).set(hashMapOf<Any, Any>())
            .addOnSuccessListener { onSuccess("success") }
            .addOnFailureListener { onFailed("failed") }
    }
}