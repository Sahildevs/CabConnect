package com.example.uberride.data.model

import com.google.gson.annotations.SerializedName

data class BookCabRequest (

    @SerializedName("cars_id")
    var carId: Int? = null,

    @SerializedName("users_id")
    var customerId: Int? = null,

    @SerializedName("start")
    var startPoint: PickupLocationData,

    @SerializedName("end")
    var endPoint: DropLocationData

)