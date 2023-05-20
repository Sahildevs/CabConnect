package com.example.uberride.ui.landing

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.uberride.R
import com.example.uberride.data.model.CabData
import com.example.uberride.databinding.FragmentLandingMapsBinding
import com.example.uberride.ui.landing.adapters.NearbyCabListAdapter
import com.example.uberride.ui.landing.bottomsheets.AddDropLocationBottomSheet
import com.example.uberride.ui.landing.bottomsheets.NearbyCabListBottomSheet
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LandingMapsFragment : Fragment(), AddDropLocationBottomSheet.Callback {

    lateinit var binding: FragmentLandingMapsBinding

    private lateinit var currentLocation: Location
    private lateinit var mMap: GoogleMap
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private val REQUEST_CODE = 101


    private val landingViewModel: LandingViewModel by activityViewModels()

    lateinit var addDropLocationBottomSheet: AddDropLocationBottomSheet
    lateinit var nearbyCabListBottomSheet: NearbyCabListBottomSheet


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mFusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentLandingMapsBinding.inflate(inflater, container, false)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Here we find the map fragment and inflate it, once inflated it calls a callback
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        onClick()
        serviceObserver()

    }


    /** Network Call */
    private fun getNearbyCabs() {
        lifecycleScope.launch {
            landingViewModel.getNearbyCabsServiceCall()
        }
    }




    private fun serviceObserver() {

        landingViewModel.responseNearbyCabs.observe(viewLifecycleOwner) { result ->
            if (result.body() != null) {

                Log.d("CABS", "${result.body()!!.cabs}")
                showNearbyCabBottomSheet(result.body()!!.cabs)
            }
            else {
                Toast.makeText(requireContext(), "ERROR", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun onClick() {

        binding.fabSearch.setOnClickListener {

            stopLocationUpdates()
            getLastKnownLocation()
            showAddDropLocationBottomSheet()
        }
    }




    private fun showAddDropLocationBottomSheet() {
        addDropLocationBottomSheet = AddDropLocationBottomSheet(this)
        addDropLocationBottomSheet.show(childFragmentManager, null)
        addDropLocationBottomSheet.isCancelable = true
    }


    private fun showNearbyCabBottomSheet(list: ArrayList<CabData>) {
        nearbyCabListBottomSheet = NearbyCabListBottomSheet(list)
        nearbyCabListBottomSheet.show(childFragmentManager, null)
        addDropLocationBottomSheet.isCancelable = true
    }














    /** Once the map is ready this callback will be triggered, and here we call a method to check for the permissions */
    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        mMap = googleMap
        checkForLocationPermission()

    }


    /** Here we actually check the permission, if granted we call the get location method or else we ask for the permissions*/
    private fun checkForLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            getUpdatedLocation()

        } else {

            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE
            )
        }


    }


    /** Here we get the location after every few seconds when all necessary permissions have been granted */
    @SuppressLint("MissingPermission")
    private fun getUpdatedLocation() {

        mMap.isMyLocationEnabled = true
        locationRequest = LocationRequest.create().apply {
            interval = 10000  //10 Seconds
            fastestInterval = 5000  //5 seconds
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        }

        //We pass this object to the requestLocationUpdates method of mFusedLocationProviderClient defined
        // in the startLocationUpdates() method to start receiving location updates.
        locationCallback = object : LocationCallback() {

            // The onLocationResult() method will be called whenever the device's location is updated based on
            //the interval and priority you set in the location request. It will also zoom to the updated location on the map.
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.apply {
                    //Storing the fetched latitude & longitude in the viewModel
                    val currentLatitude = this.latitude
                    val currentLongitude = this.longitude

                    //Once we have the updated location we track the user on the map
                    val latLng = LatLng(currentLatitude, currentLongitude)

                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))
                }
            }
        }

        startLocationUpdates()

    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        mFusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }


    /** Stop location updates*/
    private fun stopLocationUpdates() {
        mFusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation() {
        mFusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location ->

                if (location != null) {

                    // Got last known location. In some rare situations this can be null.
                    currentLocation = location

                    //Store the pickup location
                    landingViewModel.pickUpLat = currentLocation.latitude
                    landingViewModel.pickUpLng = currentLocation.longitude

                    //Add marker on the pickup location
                    val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)


                    var marker = mMap.addMarker(MarkerOptions().position(latLng).title("You are here."))

                    if (marker != null) {
                        mMap.clear()

                        marker = mMap.addMarker(MarkerOptions().position(latLng).title("You are here."))

                    }


                } else {
                    Toast.makeText(requireContext(), "Error detecting location", Toast.LENGTH_SHORT)
                        .show()

                }


            }
    }




    //This method will search the entered drop location add a marker to it
    override fun searchDropLocation(dropLocation: String) {

        //This will store all the matching addresses for the drop location
        var addressList: List<Address>? = null

        val geocoder = Geocoder(requireContext())
        addressList = geocoder.getFromLocationName(dropLocation, 1)

        val address = addressList!![0]
        val latLng = LatLng(address.latitude, address.longitude)

        //Storing drop location in the view model
        landingViewModel.dropLat = address.latitude
        landingViewModel.dropLng = address.longitude

        //Adding a marker on the map
        mMap.addMarker(MarkerOptions().position(latLng).title("Drop off"))
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))

        addDropLocationBottomSheet.dismiss()

        //making a network call
        getNearbyCabs()

    }







    // This will be called after we have asked for the permissions and user reacts on our request, i.e granted or not
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE) {
            //Once the permissions has been granted
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                //getCurrentLocation()
                getUpdatedLocation()

            }
        }

    }

}