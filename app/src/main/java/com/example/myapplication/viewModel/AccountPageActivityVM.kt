package com.example.myapplication.viewModel

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.UserDataModel
import com.example.myapplication.model.api.Api
import com.example.myapplication.view.LoginActivity
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountPageActivityVM : ViewModel() {
    var isCorrectPassword = MutableLiveData<Boolean>()
    private var api =  Api.create()

     fun checkPassword(email:String, password:String,map: HashMap<String,String>, context: Context) {
         val sharedPreferencesDataBase = SharedPreferencesDataBase(context)
         val token = sharedPreferencesDataBase.getToken()
        if (password.isEmpty()) {
            Toast.makeText(context, "Write Your Password", Toast.LENGTH_SHORT)
                .show()
        } else {
            api.loginUser(Api.KEY, email, password)
                .enqueue(object : Callback<UserDataModel> {
                    override fun onResponse(
                        call: Call<UserDataModel>, response: Response<UserDataModel>
                    ) {
                        if (response.isSuccessful) {
                            if (response.body()?.token != null) {
                           updateData(token, map, context)
                            } else println("Something incorrect")
                        } else Toast.makeText(
                            context, "Incorrect password", Toast.LENGTH_SHORT).show()
                    }
                    override fun onFailure(call: Call<UserDataModel>, t: Throwable) {
                        println(t.message)
                    }
                })
        }
    }
   private fun updateData(token: String, map: HashMap<String, String>, context: Context) {
        api.updateUserData(Api.KEY, token, map).enqueue(object : Callback<UserDataModel> {
            override fun onResponse(call: Call<UserDataModel>, response: Response<UserDataModel>) {
                if (response.isSuccessful) {
                   isCorrectPassword.value = true
                    Toast.makeText(context, "Data is loaded", Toast.LENGTH_SHORT)
                        .show()
                } else println("ERROR")
            }

            override fun onFailure(call: Call<UserDataModel>, t: Throwable) {
                println(t.message)
            }
        })
    }
    fun logOut(context: Context){
        val sharedPreferencesDataBase =  SharedPreferencesDataBase(context)
        sharedPreferencesDataBase.delToken()
        val intent = Intent(context, LoginActivity::class.java)
        context.startActivity(intent)
    }

     fun uploadImage(body : MultipartBody.Part, context: Context) {
         val sharedPreferencesDataBase = SharedPreferencesDataBase(context)
         val token = sharedPreferencesDataBase.getToken()
        val call = api.upload(Api.KEY, token, body)
        call!!.enqueue(object : Callback<UserDataModel?> {
            override fun onResponse(
                call: Call<UserDataModel?>, response: Response<UserDataModel?>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Image Successfully added", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Something gone incorrect", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<UserDataModel?>, t: Throwable) {
                println(t.message)
            }
        })
    }
}