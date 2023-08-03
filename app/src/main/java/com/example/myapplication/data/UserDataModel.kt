package com.example.myapplication.data

import com.google.gson.annotations.SerializedName

data class UserDataModel(
    @SerializedName("first_name")
    var first_name: String,

    @SerializedName("last_name")
    var last_name: String,

    @SerializedName("email")
    var email: String,

    @SerializedName("phone")
    var phone: String,

    @SerializedName("token")
    var token: String,

    @SerializedName("profile_picture")
    var image: String
)
