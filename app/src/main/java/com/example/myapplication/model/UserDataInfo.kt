package com.example.myapplication.model

import com.google.gson.annotations.SerializedName

data class UserDataInfo(
    @SerializedName("info")
    val userDataModel: UserDataModel
)