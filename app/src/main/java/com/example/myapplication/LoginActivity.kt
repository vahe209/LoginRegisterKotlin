package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.myapplication.Colors.ColorsGet
import com.example.myapplication.Icons.GetIconFromDirectory
import com.example.myapplication.Retrofit.RetrofitClient.IMG_BASE_URL
import com.example.myapplication.Retrofit.RetrofitClient.KEY
import com.example.myapplication.Retrofit.RetrofitClient.api
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.model.UserDataInfo
import com.example.myapplication.model.UserDataModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var bindingClass: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingClass = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindingClass.root)
        getBackgroundColorAndIcon()
        bindingClass.registerBtn.setOnClickListener {
            createRegisterActivity()
        }
            bindingClass.loginBtn.setOnClickListener {
                if (!bindingClass.emailEdText.text.isNullOrEmpty() && !bindingClass.passwordEdText.text.isNullOrEmpty()){
                checkData()
        }else{
            Toast.makeText(this@LoginActivity,"Fill All Fields", Toast.LENGTH_SHORT).show()
        }
            }
        val sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        if (!token.isNullOrEmpty()) {
            signIn(token)
        }
    }
    private fun signIn(token:String) {
        api.checkTokenForLogin(KEY, token).enqueue(object : Callback<UserDataInfo> {
            override fun onResponse(
                call: Call<UserDataInfo>, response: Response<UserDataInfo>) =
                if (response.isSuccessful) {
                    createAccountPageActivity(response.body()!!.userDataModel.first_name,
                        response.body()!!.userDataModel.last_name,
                        response.body()!!.userDataModel.email,
                        response.body()!!.userDataModel.phone,
                        response.body()!!.userDataModel.image)
                } else {
                    println("Error")
                }

            override fun onFailure(call: Call<UserDataInfo>, t: Throwable) {
                println(t.message)
            }
        })
    }

    private fun checkData() {
        api.loginUser(
            KEY,
            bindingClass.emailEdText.text.toString(),
            bindingClass.passwordEdText.text.toString()
        ).enqueue(object : Callback<UserDataModel> {
            override fun onResponse(call: Call<UserDataModel>, response: Response<UserDataModel>) {
                if (response.isSuccessful) {
                    val sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("token", response.body()!!.token).apply()
                    createAccountPageActivity(
                        response.body()!!.first_name,
                        response.body()!!.last_name,
                        response.body()!!.email,
                        response.body()!!.phone,
                        response.body()!!.image)
                } else Toast.makeText(
                    this@LoginActivity, "Incorrect Email or Password", Toast.LENGTH_SHORT
                ).show()
            }

            override fun onFailure(call: Call<UserDataModel>, t: Throwable) {
                println(t.message)
            }
        })
    }

    private fun createAccountPageActivity(
        firstName: String, lastName: String, email: String, phone: String, file:String) {
        val intent = Intent(this@LoginActivity, AccountPageActivity::class.java)
        intent.putExtra("first_name", firstName)
        intent.putExtra("last_name", lastName)
        intent.putExtra("email", email)
        intent.putExtra("phone", phone)
        intent.putExtra("image", file)
        startActivity(intent)
    }

    private fun createRegisterActivity() {
        val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun getBackgroundColorAndIcon() {
        val colorList = HashMap<String, String>()
        api.getColors(KEY).enqueue(object : Callback<ColorsGet?> {
            override fun onResponse(call: Call<ColorsGet?>, response: Response<ColorsGet?>) {
                for (x in 0 until response.body()!!.allColors.size) {
                    colorList[response.body()!!.allColors[x].name] = response.body()!!.allColors[x].value
                }
                val colorBg = "#${colorList["accentMain"].toString()}"
                bindingClass.mainActivityConstraint.setBackgroundColor(colorBg.toColorInt())
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
                    val iconUrl: String = IMG_BASE_URL + response.body()?.icons?.icon
                    Glide.with(this@LoginActivity).load(iconUrl).into(bindingClass.icon)
                    bindingClass.mainActivityConstraint.isVisible = true
                }
            }
            override fun onFailure(call: Call<GetIconFromDirectory>, t: Throwable) {
                println(t.message)
            }
        })
    }
}
