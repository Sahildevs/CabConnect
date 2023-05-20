package com.example.uberride.data.model

import com.google.gson.annotations.SerializedName

data class AddNewUserRequest (

    @SerializedName("name")
    var name: String? = null,

    @SerializedName("phone")
    var phone: String? = null

)