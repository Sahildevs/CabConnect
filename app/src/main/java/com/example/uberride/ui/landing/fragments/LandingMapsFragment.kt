package com.example.uberride.ui.landing.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.uberride.R
import com.example.uberride.data.model.CabData
import com.example.uberride.databinding.FragmentLandingMapsBinding
import com.example.uberride.ui.landing.LandingViewModel
import com.example.uberride.ui.landing.bottomsheets.AddDropLocationBottomSheet
import com.example.uberride.ui.landing.bottomsheets.NearbyCabListBottomSheet
import com.example.uberride.ui.landing.bottomsheets.RequestProcessingBottomSheet
import com.example.uberride.utils.FirebaseUtils
import com.example.uberride.utils.LocationUtils
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LandingMapsFragment : Fragment(), AddDropLocationBottomSheet.Callback, NearbyCabListBottomSheet.Callback {

    lateinit var binding: FragmentLandingMapsBinding

    private lateinit var locationUtils: LocationUtils
    private lateinit var mMap: GoogleMap

    private lateinit var addDropLocationBottomSheet: AddDropLocationBottomSheet
    private lateinit var nearbyCabListBottomSheet: NearbyCabListBottomSheet
    private lateinit var requestProcessingBottomSheet: RequestProcessingBottomSheet

    private val landingViewModel: LandingViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //This class handles all location related operation
        locationUtils = LocationUtils(requireContext())

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

    /** Network Call */
    private fun bookMyCab() {
        lifecycleScope.launch {
            landingViewModel.bookMyCabServiceCall()
        }

        //Firebase service call
        landingViewModel.addRideRequest()
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


        landingViewModel.responseBookMyCab.observe(viewLifecycleOwner) { result->

            if (result != null) {
                Log.d("BOOKING", "${result.body()?.status}")

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
        nearbyCabListBottomSheet = NearbyCabListBottomSheet(this, list)
        nearbyCabListBottomSheet.show(childFragmentManager, null)
        addDropLocationBottomSheet.isCancelable = true
    }

    private fun showLoadingBottomSheet() {
        requestProcessingBottomSheet = RequestProcessingBottomSheet()
        requestProcessingBottomSheet.show(childFragmentManager, null)
        requestProcessingBottomSheet.isCancelable = false
    }









    // Once the map is ready this callback will be triggered, and here we call a method to check for the permissions
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

    //Check location permissions
    private fun checkForLocationPermission() {

        if (locationUtils.checkLocationPermission()) {
            getUpdatedLocation()
        }
        else {
            requestLocationPermission()
        }

    }


    //Request for location permissions
    private fun requestLocationPermission() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        locationPermissionLauncher.launch(permissions)
    }


    //Start receiving location updates
    @SuppressLint("MissingPermission")
    private fun getUpdatedLocation() {

        mMap.isMyLocationEnabled = true

        locationUtils.startLocationUpdates(object : LocationUtils.LocationListenerCallback{
            override fun onLocationChanged(location: Location) {
                //Handle location updates

                val latLng = LatLng(location.latitude, location.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))
            }
        })

    }


    //Stop receiving location updates
    private fun stopLocationUpdates() {
        locationUtils.stopLocationUpdates()
    }


    //Receive last known location
    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation() {

        locationUtils.getLastKnownLocation(object : LocationUtils.LocationReceiverCallback{
            override fun onLocationReceived(location: Location?) {

                if (location != null) {
                    //Handle last known location

                    val iconBitmap = BitmapFactory.decodeResource(resources, R.drawable.pickup_marker)
                    val customMarker = BitmapDescriptorFactory.fromBitmap(iconBitmap)

                    //Add a marker
                    val latLng = LatLng(location.latitude, location.longitude)
                    var marker = mMap.addMarker(MarkerOptions().position(latLng).title("You are here.").icon(customMarker))
                    if (marker != null) {
                        mMap.clear()
                        marker = mMap.addMarker(MarkerOptions().position(latLng).title("You are here.").icon(customMarker))
                    }

                    //Store pickup location
                    landingViewModel.pickUpLat = location.latitude
                    landingViewModel.pickUpLng = location.longitude
                }
                else {
                    Toast.makeText(requireContext(), "Error detecting location", Toast.LENGTH_SHORT).show()
                }
            }
        })

    }


    //Search entered drop location
    override fun searchDropLocation(dropLocation: String) {

        val iconBitmap = BitmapFactory.decodeResource(resources, R.drawable.drop_marker)
        val customMarker = BitmapDescriptorFactory.fromBitmap(iconBitmap)

        val address = locationUtils.searchDropLocation(dropLocation)!![0]
        val latLng = LatLng(address.latitude, address.longitude)

        //Adding a marker on the map
        mMap.addMarker(MarkerOptions().position(latLng).title("Drop off").icon(customMarker))
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))

        //Storing drop location in the view model
        landingViewModel.dropLat = address.latitude
        landingViewModel.dropLng = address.longitude

        addDropLocationBottomSheet.dismiss()

        //making a network call
        getNearbyCabs()

    }


    //Book cab request
    override fun requestCab() {

        bookMyCab()
        nearbyCabListBottomSheet.dismiss()
        showLoadingBottomSheet()

    }



    //Checks permission grant result
    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allPermissionsGranted = permissions.all { it.value }

            if (allPermissionsGranted) {
                // Start location updates or perform other actions requiring location permission
                getUpdatedLocation()
            } else {
                // Handle the case where some or all permissions were not granted
                // You can show a message or take appropriate action here
            }
        }


}