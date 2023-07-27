package com.example.myapplication.Colors

import com.google.gson.annotations.SerializedName

data class ColorsGet(
    @SerializedName("custom_colors")
    val allColors: MutableList<Colors>
)

