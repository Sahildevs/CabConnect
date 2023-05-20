package com.example.uberride.data.model

import com.google.gson.annotations.SerializedName

data class PickupLocationData(

    @SerializedName("lat")
    val lat: Double? = null,

    @SerializedName("lng")
    val lng: Double? = null,

)