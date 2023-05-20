package com.example.uberride.data.model

import com.google.gson.annotations.SerializedName

data class NearbyCabsResponse(

    @SerializedName("cabs")
    val cabs: ArrayList<CabData>
)