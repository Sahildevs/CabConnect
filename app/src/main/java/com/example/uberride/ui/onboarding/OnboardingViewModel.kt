package com.example.uberride.ui.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.uberride.data.model.AddNewUserRequest
import com.example.uberride.data.model.AddNewUserResponse
import com.example.uberride.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(private val repository: MainRepository) : ViewModel() {


    /** User details */
    var name: String? = null
    var phoneNumber: String? = null



    private var _responseAddNewUser= MutableLiveData<Response<AddNewUserResponse>>()
    val responseAddNewUser: LiveData<Response<AddNewUserResponse>> = _responseAddNewUser



    /** Create new user network call */
    suspend fun addNewUserServiceCall() {
        val request = AddNewUserRequest(
            name = name, phone = phoneNumber
        )

        val res = repository.addNewUser(request)
        _responseAddNewUser.postValue(res)
    }


}