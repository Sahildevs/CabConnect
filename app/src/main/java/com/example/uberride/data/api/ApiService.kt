package com.example.uberride.data.api

import com.example.uberride.data.model.AddNewUserRequest
import com.example.uberride.data.model.AddNewUserResponse
import com.example.uberride.data.model.BookCabRequest
import com.example.uberride.data.model.BookCabResponse
import com.example.uberride.data.model.NearbyCabsResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


interface ApiService {

    /** Add new user */
    @POST("add_new_user")
    suspend fun addNewUser(@Body request: AddNewUserRequest): Response<AddNewUserResponse>

    /** Get nearby cabs */
    @GET("get_nearby_cars")
    suspend fun getNearbyCabs(@Query("location") location: String): Response<NearbyCabsResponse>

    /** Book selected cab */
    @POST("book_car")
    suspend fun bookMyCab(@Body request: BookCabRequest): Response<BookCabResponse>












    companion object {
        private const val BASE_URL = "https://x8ki-letl-twmt.n7.xano.io/api:KkRqjwUP/"

        var apiService: ApiService? = null

        fun getInstance(): ApiService {
            if (apiService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                apiService = retrofit.create(ApiService::class.java)
            }
            return apiService!!
        }
    }
}