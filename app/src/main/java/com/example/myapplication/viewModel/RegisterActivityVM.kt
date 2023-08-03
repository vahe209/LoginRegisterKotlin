package com.example.myapplication.viewModel

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.UserDataModel
import com.example.myapplication.model.api.Api
import com.example.myapplication.model.api.Api.Companion.KEY
import com.example.myapplication.model.api.Api.Companion.create
import com.example.myapplication.view.AccountPageActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Matcher
import java.util.regex.Pattern

class RegisterActivityVM() : ViewModel() {
    private val api : Api =  create()
    var map: MutableMap<String, String> = mutableMapOf()
    var isValidPassword = MutableLiveData<Boolean>()
    var isValidEmail = MutableLiveData<Boolean>()
    private  var password :String? = null

    fun post(context: Context) {
        val sharedPreferencesDataBase = SharedPreferencesDataBase(context)
        if (map.isNotEmpty()) {
            password = map["password"]
            api.postData(KEY, map).enqueue(object : Callback<UserDataModel> {
                override fun onResponse(
                    call: Call<UserDataModel>,
                    response: Response<UserDataModel>
                ) {
                    if (response.isSuccessful) {
                        createAccountPageActivity(map, context)
                        sharedPreferencesDataBase.saveToken(response.body()!!.token)
                    } else {
                        println("Error")
                    }
                }
                override fun onFailure(call: Call<UserDataModel>, t: Throwable) {
                    println(t.message)
                }
            })
        }
    }

    private fun createAccountPageActivity(map: MutableMap<String, String>, context: Context) {
        val intent = Intent(context, AccountPageActivity::class.java)
        intent.putExtra("first_name", map["first_name"])
        intent.putExtra("last_name", map["last_name"])
        intent.putExtra("email", map["email"])
        intent.putExtra("phone", map["phone"])
        startActivity(context, intent, null)
    }
     fun checkPassword(password: String, context: Context){
            val regex = "^(?=.*\\d)" +
                    "(?=.*[a-z])(?=.*[A-Z])" +
                    "(?=.*[!@#$%^&*()_+~`<>?:{}])" +
                    "(?=\\S+$).{8,20}$"
            val p: Pattern = Pattern.compile(regex)
            val m: Matcher = p.matcher(password)
            if (m.matches()) {
                isValidPassword.value = true
            } else {
                Toast.makeText(
                    context,"Password must be contain Z-z, 1 capital letter and symbol", Toast.LENGTH_SHORT).show()
            }
        }
     fun checkEmail(email:String, context: Context) {
        val regex = "^[A-Za-z\\d+_.-]+@(.+)$"
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(email)
        if (matcher.matches()) {
            isValidEmail.value = true
        } else {
            Toast.makeText(context, "Invalid Email", Toast.LENGTH_SHORT).show()
        }
    }
    }

