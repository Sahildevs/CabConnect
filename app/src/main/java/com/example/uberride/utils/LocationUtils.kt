package com.example.uberride.utils

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*

class LocationUtils(private val context: Context) {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var locationListener: LocationListenerCallback? = null
    private var locationReceiver: LocationReceiverCallback? = null


    //Checks whether the required permissions are granted or not and returns a boolean value
    // indicating whether the permissions are granted.
    fun checkLocationPermission(): Boolean {

        return ActivityCompat.checkSelfPermission(
            context, android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    context, android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

    }


    //Starts receiving location updates on a regular interval
    fun startLocationUpdates(locationListener: LocationListenerCallback) {
        this.locationListener = locationListener

        if (!checkLocationPermission()){
            //Handle permission not granted
            return
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)


        locationRequest = LocationRequest()
        locationRequest.interval = 10000 //10sec
        locationRequest.fastestInterval = 5000  //5sec
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY


        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let {
                    locationListener.onLocationChanged(it)
                }
            }
        }

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

    }


    //Stops receiving location updates
    fun stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }


    //Retrieves the last known location of the device
    fun getLastKnownLocation(locationReceiver: LocationReceiverCallback) {
        this.locationReceiver = locationReceiver

        if (!checkLocationPermission()) {
            //Handle permission not granted
            locationReceiver.onLocationReceived(null) //indicating that the last known location cannot be retrieved due to lack of permissions.
        }

        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            //Handle last known location
            locationReceiver.onLocationReceived(location)
        }

    }


    //Returns the lat lng for entered location
    fun searchDropLocation(location: String): List<Address>? {

        //This will store all the matching addresses for the drop location
        var addressList: List<Address>? = null

        val geocoder = Geocoder(context)
        addressList = geocoder.getFromLocationName(location, 1)
        return addressList

    }




    interface LocationListenerCallback {
        fun onLocationChanged(location: Location)
    }

    interface LocationReceiverCallback {
        fun onLocationReceived(location: Location?)
    }


}