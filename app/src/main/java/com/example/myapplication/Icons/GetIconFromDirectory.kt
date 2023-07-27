package com.example.myapplication.Icons

import com.google.gson.annotations.SerializedName

data class GetIconFromDirectory (
    @SerializedName("media")
    val icons: Icons
)