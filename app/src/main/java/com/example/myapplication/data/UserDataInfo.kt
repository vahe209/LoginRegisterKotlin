package com.example.myapplication.data

import com.google.gson.annotations.SerializedName

data class UserDataInfo(
    @SerializedName("info")
    val userDataModel: UserDataModel
)