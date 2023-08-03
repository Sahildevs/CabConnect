package com.example.uberride.ui.landing.home.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.location.Location
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.uberride.R
import com.example.uberride.data.model.CabData
import com.example.uberride.databinding.FragmentLandingMapsBinding
import com.example.uberride.ui.landing.LandingViewModel
import com.example.uberride.ui.landing.home.bottomsheets.AddDropLocationBottomSheet
import com.example.uberride.ui.landing.home.bottomsheets.BookedCabDetailsBottomSheet
import com.example.uberride.ui.landing.home.bottomsheets.DestinationReachedBottomSheet
import com.example.uberride.ui.landing.home.bottomsheets.NearbyCabListBottomSheet
import com.example.uberride.ui.landing.home.bottomsheets.RequestAcceptedBottomSheet
import com.example.uberride.ui.landing.home.bottomsheets.RequestDeniedBottomSheet
import com.example.uberride.ui.landing.home.bottomsheets.RequestProcessingBottomSheet
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
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LandingMapsFragment : Fragment(), AddDropLocationBottomSheet.Callback, NearbyCabListBottomSheet.Callback,
    RequestDeniedBottomSheet.Callback, DestinationReachedBottomSheet.Callback {

    lateinit var binding: FragmentLandingMapsBinding

    private lateinit var locationUtils: LocationUtils
    private lateinit var mMap: GoogleMap

    private var pickupMarker: Marker? = null
    private var dropMarker: Marker? = null
    private var cabMarker: Marker? = null

    private lateinit var firebaseUtils: FirebaseUtils
    private lateinit var listenerRegistration: ListenerRegistration
    private lateinit var locationRegistration: ListenerRegistration

    private lateinit var addDropLocationBottomSheet: AddDropLocationBottomSheet
    private lateinit var nearbyCabListBottomSheet: NearbyCabListBottomSheet
    private lateinit var requestProcessingBottomSheet: RequestProcessingBottomSheet
    private lateinit var requestAcceptedBottomSheet: RequestAcceptedBottomSheet
    private lateinit var requestDeniedBottomSheet: RequestDeniedBottomSheet
    private lateinit var bookedCabDetailsBottomSheet: BookedCabDetailsBottomSheet
    private lateinit var destinationReachedBottomSheet: DestinationReachedBottomSheet

    private val landingViewModel: LandingViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //This class handles all location related operation
        locationUtils = LocationUtils(requireContext())

        //This class handles all firebase operations
        firebaseUtils = FirebaseUtils()

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

        startListeningRequestedRideStatus()
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
            //stopLocationUpdates()
            getLastKnownLocation()
            dropMarker?.remove()
            showAddDropLocationBottomSheet()
        }

        binding.fabCabDetails.setOnClickListener {
            showBookedCabDetailsBottomSheet()
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

    private suspend fun showRideRequestAcceptedBottomSheet() {
        requestAcceptedBottomSheet = RequestAcceptedBottomSheet()
        requestAcceptedBottomSheet.show(childFragmentManager, null)
        requestAcceptedBottomSheet.isCancelable = false
        delay(3000)
        requestAcceptedBottomSheet.dismiss()
        showBookedCabDetailsBottomSheet()

    }

    private fun showRideRequestRejectedBottomSheet() {
        requestDeniedBottomSheet = RequestDeniedBottomSheet(this)
        requestDeniedBottomSheet.show(childFragmentManager, null)
        requestDeniedBottomSheet.isCancelable = false

    }


    private fun showBookedCabDetailsBottomSheet() {
        bookedCabDetailsBottomSheet = BookedCabDetailsBottomSheet()
        bookedCabDetailsBottomSheet.show(childFragmentManager, null)
        bookedCabDetailsBottomSheet.isCancelable = false
        binding.layoutFabCabDetails.isVisible = true
        startTrackingBookedCab()

    }

    private fun showDestinationReachedBottomSheet() {
        destinationReachedBottomSheet = DestinationReachedBottomSheet(this)
        destinationReachedBottomSheet.show(childFragmentManager, null)
        destinationReachedBottomSheet.isCancelable = false

    }



    //Listens to the drivers response on your ride request
    private fun startListeningRequestedRideStatus() {
        listenerRegistration = firebaseUtils.getRequestedRideStatus(
            driverId = landingViewModel.driverId.toString(),
            status = { status ->
                when(status) {
                    "accepted" -> {
                        requestProcessingBottomSheet.dismiss()
                        //stopListeningRequestedRideStatus()
                        binding.layoutFabSearch.isVisible = false
                        lifecycleScope.launch { showRideRequestAcceptedBottomSheet() }
                    }
                    "rejected" -> {
                        requestProcessingBottomSheet.dismiss()
                        stopListeningRequestedRideStatus()
                        showRideRequestRejectedBottomSheet()
                    }
                    "completed" -> {
                        stopListeningRequestedRideStatus()
                        stopTrackingBookedCab()
                    }
                }
            }
        )
    }

    //Unregister the firestore document listener
    private fun stopListeningRequestedRideStatus() {
        listenerRegistration.remove()
    }


    //Start tracking the live location of the booked cab
    private fun startTrackingBookedCab() {

        val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_cab_track_marker)
        val bitmap = drawable?.toBitmap()

        locationRegistration = firebaseUtils.getBookedCabLocation(
            driverId = landingViewModel.driverId.toString(),
            latLng = {
                //Receiving the current/live lat lng coordinates of the cab

                Log.d("Cordinates", "LatLng: $it")
                landingViewModel.cabCurrentLocation = it

                //Remove previously added marker, if any
                cabMarker?.remove()
                //Add a marker
                cabMarker = mMap.addMarker(MarkerOptions().position(it).title("Cab").icon(BitmapDescriptorFactory.fromBitmap(bitmap!!)))

                hasCabArrived()
            }
        )
    }


    //Stop tracking the location of the booked cab
    private fun stopTrackingBookedCab() {
        locationRegistration.remove()
    }


    //Checks if the cab has reached the destination point
    private fun hasCabArrived() {

        val hasCabArrived = locationUtils.hasCabArrived(
            cabLocation = landingViewModel.cabCurrentLocation!!,
            dropLocation = LatLng(landingViewModel.dropLat!!, landingViewModel.dropLng!!)
        )

        if (hasCabArrived && !landingViewModel.isDestinationArrived) {

            showDestinationReachedBottomSheet()
            landingViewModel.isDestinationArrived = true
        }
    }


    private fun resetDataToDefault() {
        pickupMarker?.remove()
        dropMarker?.remove()
        cabMarker?.remove()
        binding.layoutFabCabDetails.isVisible = false
        binding.layoutFabSearch.isVisible = true
        landingViewModel.isDestinationArrived = false
    }





    //Book cab request
    override fun requestCab() {
        bookMyCab()
        nearbyCabListBottomSheet.dismiss()
        showLoadingBottomSheet()

    }

    //Get other nearby cabs when request denied
    override fun getOtherNearbyCabs() {
        getNearbyCabs()
        requestDeniedBottomSheet.dismiss()
    }

    //Finish trip
    override fun finishTrip() {
        resetDataToDefault()
        destinationReachedBottomSheet.dismiss()

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
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
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

                    val latLng = LatLng(location.latitude, location.longitude)

                    val iconBitmap = BitmapFactory.decodeResource(resources, R.drawable.pickup_marker)
                    val customMarker = BitmapDescriptorFactory.fromBitmap(iconBitmap)

                    //Remove previously added marker, if any
                    pickupMarker?.remove()
                    //Add a marker
                    pickupMarker = mMap.addMarker(MarkerOptions().position(latLng).title("You are here.").icon(customMarker))


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

        if (address != null) {

            val latLng = LatLng(address.latitude, address.longitude)

            //Remove previously added marker if any
            dropMarker?.remove()

            //Adding a new marker on the map
            dropMarker = mMap.addMarker(MarkerOptions().position(latLng).title("Drop off").icon(customMarker))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))

            //Storing drop location in the view model
            landingViewModel.dropLat = address.latitude
            landingViewModel.dropLng = address.longitude

            addDropLocationBottomSheet.dismiss()

            //making a network call
            getNearbyCabs()
        }
        else {
            Toast.makeText(requireContext(), "Address not found, Try Again!", Toast.LENGTH_SHORT).show()
        }


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