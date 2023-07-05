package com.example.uberride.ui.landing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.uberride.data.model.BookCabRequest
import com.example.uberride.data.model.BookCabResponse
import com.example.uberride.data.model.DropLocationData
import com.example.uberride.data.model.PickupLocationData
import com.example.uberride.data.model.NearbyCabsResponse
import com.example.uberride.data.repository.MainRepository
import com.example.uberride.utils.FirebaseUtils
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class LandingViewModel @Inject constructor(private val repository: MainRepository) : ViewModel() {


    /** User details */
    var userId: Int? = null
    var name: String? = null
    var phone: String? = null
    var pickUpLat: Double? = null
    var pickUpLng: Double?= null

    /** Book cab details */
    var carId: Int? = null
    var driverId: Int? = null
    var dropLat: Double? = null
    var dropLng: Double? = null

    private val firebaseUtils = FirebaseUtils()

    private val _responseNearbyCabs = MutableLiveData<Response<NearbyCabsResponse>>()
    val responseNearbyCabs: LiveData<Response<NearbyCabsResponse>> = _responseNearbyCabs

    private val _responseBookMyCab = MutableLiveData<Response<BookCabResponse>>()
    val responseBookMyCab: LiveData<Response<BookCabResponse>> = _responseBookMyCab



    /** Get nearby cabs service call */
    suspend fun getNearbyCabsServiceCall() {
        val locationData = PickupLocationData(lng = pickUpLng, lat = pickUpLat)

        // Convert locationData to JSON string
        val request = Gson().toJson(locationData)

        val res = repository.getNearbyCabs(request)
        _responseNearbyCabs.postValue(res)

    }

    /** Book cab service call */
    suspend fun bookMyCabServiceCall() {

        val request = BookCabRequest(
            carId = carId,
            customerId = userId,
            startPoint = PickupLocationData(lat = pickUpLat, lng = pickUpLng),
            endPoint = DropLocationData(lat = dropLat, lng = dropLng)
        )

        val res = repository.bookMyCab(request)
        _responseBookMyCab.postValue(res)
    }

    /** Add ride request firebase service call */
    fun addRideRequest() {
        firebaseUtils.addRideRequest(
            driverId = driverId.toString(),
            onSuccess = {},
            onFailed = {}
        )
    }




}