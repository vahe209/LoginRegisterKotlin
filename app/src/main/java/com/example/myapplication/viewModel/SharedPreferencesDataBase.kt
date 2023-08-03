package com.example.myapplication.viewModel

import android.content.Context

class SharedPreferencesDataBase (context: Context) {
    private val sharedPreferences = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
    private val editor = sharedPreferences.edit()
    private lateinit var  token : String

    fun saveToken(token: String){
        editor.putString("token", token).apply()
         this.token = token
    }
    fun getToken(): String{
        token = sharedPreferences.getString("token", null).toString()
        return token
    }

    fun delToken() {
        editor.remove("token").apply()
    }

}