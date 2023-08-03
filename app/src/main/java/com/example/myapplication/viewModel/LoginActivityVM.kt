package com.example.myapplication.viewModel

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.ColorsGet
import com.example.myapplication.data.GetIconFromDirectory
import com.example.myapplication.data.UserDataInfo
import com.example.myapplication.data.UserDataModel
import com.example.myapplication.model.api.Api
import com.example.myapplication.model.api.Api.Companion.KEY
import com.example.myapplication.view.AccountPageActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivityVM : ViewModel() {

    private val api: Api = Api.create()
    val bgColor: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val icon: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    fun getBackgroundColorAndIcon() {
        val colorList = HashMap<String, String>()
        api.getColors(KEY).enqueue(object : Callback<ColorsGet?> {
            override fun onResponse(call: Call<ColorsGet?>, response: Response<ColorsGet?>) {
                for (x in 0 until response.body()!!.allColors.size) {
                    colorList[response.body()!!.allColors[x].name] =
                        response.body()!!.allColors[x].value
                }
                bgColor.value = "#${colorList["accentMain"].toString()}"
            }

            override fun onFailure(call: Call<ColorsGet?>, t: Throwable) {
                println(t.message)
            }
        })

        api.getIconMainPage(KEY).enqueue(object : Callback<GetIconFromDirectory> {
            override fun onResponse(
                call: Call<GetIconFromDirectory>, response: Response<GetIconFromDirectory>
            ) {
                if (response.isSuccessful) {
                    icon.value = Api.IMG_BASE_URL + response.body()?.icons?.icon
                }

            }

            override fun onFailure(call: Call<GetIconFromDirectory>, t: Throwable) {
                println(t.message)
            }
        })
    }

    fun checkData(email: String, password: String, context: Context) {
        val sharedPreferencesDataBase = SharedPreferencesDataBase(context)
        api.loginUser(Api.KEY, email, password).enqueue(object : Callback<UserDataModel> {
            override fun onResponse(
                call: Call<UserDataModel>, response: Response<UserDataModel>
            ) {
                if (response.isSuccessful) {
                    createAccountPageActivity(
                        response.body()!!.first_name,
                        response.body()!!.last_name,
                        response.body()!!.email,
                        response.body()!!.phone,
                        response.body()!!.image,
                        context
                    )
                    sharedPreferencesDataBase.saveToken(token = response.body()!!.token)
                } else Toast.makeText(
                    context, "Incorrect Email or Password", Toast.LENGTH_SHORT
                ).show()
            }

            override fun onFailure(call: Call<UserDataModel>, t: Throwable) {
                println(t.message)
            }
        })
    }

    private fun createAccountPageActivity(firstName: String, lastName: String, email: String, phone: String, file: String, context: Context) {
        val intent = Intent(context, AccountPageActivity::class.java)
        intent.putExtra("first_name", firstName)
        intent.putExtra("last_name", lastName)
        intent.putExtra("email", email)
        intent.putExtra("phone", phone)
        intent.putExtra("image", file)
        startActivity(context, intent, null)
    }

    fun checkToken(context: Context) {
        val sharedPreferencesDataBase = SharedPreferencesDataBase(context)
        val token = sharedPreferencesDataBase.getToken()
        if (token.isNotEmpty()) {
            api.checkTokenForLogin(KEY, token).enqueue(object : Callback<UserDataInfo> {
                override fun onResponse(
                    call: Call<UserDataInfo>, response: Response<UserDataInfo>
                ) = if (response.isSuccessful) {
                    createAccountPageActivity(
                        response.body()!!.userDataModel.first_name,
                        response.body()!!.userDataModel.last_name,
                        response.body()!!.userDataModel.email,
                        response.body()!!.userDataModel.phone,
                        response.body()!!.userDataModel.image,
                        context
                    )
                } else {
                    println("Error")
                }

                override fun onFailure(call: Call<UserDataInfo>, t: Throwable) {
                    println(t.message)
                }
            })
        }
    }
}