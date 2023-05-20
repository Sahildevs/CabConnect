package com.example.uberride.data.model

import com.google.gson.annotations.SerializedName

data class AddNewUserResponse(

    @SerializedName("user")
    val user: User
)