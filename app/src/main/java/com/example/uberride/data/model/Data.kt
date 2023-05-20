package com.example.uberride.data.model

import com.google.gson.annotations.SerializedName

data class Data(

    @SerializedName("lat")
    val lat: Double,

    @SerializedName("lng")
    val lng: Double

)