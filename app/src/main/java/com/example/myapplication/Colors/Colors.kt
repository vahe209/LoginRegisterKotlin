package com.example.myapplication.Colors

import com.google.gson.annotations.SerializedName

data class Colors(
    @SerializedName("value")
    val value:String,
    @SerializedName("name")
    val name:String
)
