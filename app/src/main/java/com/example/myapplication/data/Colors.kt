package com.example.myapplication.data

import com.google.gson.annotations.SerializedName

data class Colors(
    @SerializedName("value")
    val value:String,
    @SerializedName("name")
    val name:String
)
