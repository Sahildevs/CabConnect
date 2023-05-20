package com.example.uberride.data.model

import com.google.gson.annotations.SerializedName

data class DriverData(

    @SerializedName("name")
    val name: String,

    @SerializedName("phone")
    val phone: String
)