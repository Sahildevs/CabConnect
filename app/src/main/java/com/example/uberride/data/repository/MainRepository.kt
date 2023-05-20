package com.example.uberride.data.repository

import com.example.uberride.data.api.ApiService
import com.example.uberride.data.model.AddNewUserRequest
import com.example.uberride.data.model.AddNewUserResponse
import com.example.uberride.data.model.BookCabRequest
import com.example.uberride.data.model.BookCabResponse
import com.example.uberride.data.model.NearbyCabsResponse
import retrofit2.Response
import javax.inject.Inject

class MainRepository @Inject constructor(private val apiService: ApiService) {


    suspend fun addNewUser(request: AddNewUserRequest): Response<AddNewUserResponse> {
        return apiService.addNewUser(request)
    }

    suspend fun getNearbyCabs(request: String): Response<NearbyCabsResponse> {
        return apiService.getNearbyCabs(request)
    }

    suspend fun bookMyCab(request: BookCabRequest): Response<BookCabResponse> {
        return apiService.bookMyCab(request)
    }








}