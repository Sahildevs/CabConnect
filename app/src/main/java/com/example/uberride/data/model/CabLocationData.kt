package com.example.uberride.data.model

import com.google.gson.annotations.SerializedName

data class CabLocationData(

    @SerializedName("data")
    val data: Data,

    @SerializedName("type")
    val type: String
)