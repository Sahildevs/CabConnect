package com.example.uberride.ui.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.uberride.data.model.AddNewUserRequest
import com.example.uberride.data.model.AddNewUserResponse
import com.example.uberride.data.repository.MainRepository
import com.example.uberride.utils.SharedPrefUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(private val repository: MainRepository, private val sharedPrefUtils: SharedPrefUtils) : ViewModel() {


    /** User details */
    var name: String? = null
    var phoneNumber: String? = null
    var userId: Int? = null

    private var _responseCheckUserExists = MutableLiveData<Response<AddNewUserResponse>>()
    val responseCheckUserExists: LiveData<Response<AddNewUserResponse>> = _responseCheckUserExists

    private var _responseAddNewUser= MutableLiveData<Response<AddNewUserResponse>>()
    val responseAddNewUser: LiveData<Response<AddNewUserResponse>> = _responseAddNewUser



    /** Check user exists network call */
    suspend fun checkUserExists() {
        val request = phoneNumber?.toLong()!!

        val res = repository.checkUserExists(request)
        _responseCheckUserExists.postValue(res)

    }

    /** Register new user network call */
    suspend fun addNewUserServiceCall() {
        val request = AddNewUserRequest(
            name = name, phone = phoneNumber
        )

        val res = repository.addNewUser(request)
        _responseAddNewUser.postValue(res)
    }


    //Add data to shared preference
    fun addDataToSharedPref() {
        sharedPrefUtils.addData("NAME", name!!)
        sharedPrefUtils.addData("PHONE", phoneNumber!!)
        sharedPrefUtils.addData("USER_ID", userId!!)

    }

}