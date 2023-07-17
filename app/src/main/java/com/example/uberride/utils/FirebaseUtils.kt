package com.example.uberride.utils

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class FirebaseUtils {

    private val db = FirebaseFirestore.getInstance()

    //Add a ride request to the firestore collection
    fun addRideRequest(driverId: String, map: Map<String, String>, onSuccess: (String) -> Unit, onFailed: (String) -> Unit) {
        db.collection("Ride Requests")
            .document(driverId)
            .set(map)
            .addOnSuccessListener { onSuccess("success") }
            .addOnFailureListener { onFailed("failed") }
    }


    //Listen status of the requested ride
    fun getRequestedRideStatus(driverId: String, status: (String) -> Unit)
    : ListenerRegistration {

        val collectionRef = db.collection("Ride Requests")
        val documentRef = collectionRef.document(driverId)

        return documentRef.addSnapshotListener { documentSnapshot, error ->
            if (documentSnapshot!= null) {

                when(documentSnapshot["status"]) {
                    "ACCEPTED" -> status("accepted")
                    "REJECTED" -> status("rejected")
                }

            }
        }
    }
}