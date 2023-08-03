package com.example.myapplication.data

import com.google.gson.annotations.SerializedName

data class GetIconFromDirectory (
    @SerializedName("media")
    val icons: Icons
)