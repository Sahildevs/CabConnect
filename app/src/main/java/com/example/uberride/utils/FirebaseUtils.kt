package com.example.uberride.utils

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlin.math.ln

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


    //Get the booked cab location live coordinates
    fun getBookedCabLocation(driverId: String, latLng: (LatLng) -> Unit): ListenerRegistration {
        val collectionRef = db.collection("Active Drivers")
        val documentRef = collectionRef.document(driverId)

        //The addSnapshotListener function on the documentRef to register a listener that will be triggered
        // whenever there are changes to the document
        return documentRef.addSnapshotListener { documentSnapshot, error ->
            if (documentSnapshot != null ) {

                Log.d("Cordinates", "*******************${documentSnapshot["Lat"]}")

                val lat: String = documentSnapshot["Lat"].toString()
                val lng: String = documentSnapshot["Lng"].toString()

                //convert the location coordinates to latLng
                val latLng = LatLng(lat.toDouble(), lng.toDouble())

                latLng(latLng)

            }

        }
    }
}