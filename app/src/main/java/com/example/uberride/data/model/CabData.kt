package com.example.uberride.data.model

import com.google.gson.annotations.SerializedName

data class CabData(

    @SerializedName("id")
    val id: Int,

    @SerializedName("model")
    val model: String,

    @SerializedName("number")
    val numberPlate: String,

    @SerializedName("image")
    val image: String,

    @SerializedName("location")
    val location: CabLocationData,

    @SerializedName("state")
    val state: String,

    @SerializedName("drivers_id")
    val drivers_id: Int,

    @SerializedName("driver")
    val driver: DriverData,

    var isSelected: Boolean= false

)