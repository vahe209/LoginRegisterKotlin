package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.Retrofit.RetrofitClient.KEY
import com.example.myapplication.Retrofit.RetrofitClient.api
import com.example.myapplication.databinding.ActivityRegisterBinding
import com.example.myapplication.model.UserDataModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Matcher
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {
    private lateinit var bindingClass: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingClass = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(bindingClass.root)
        bindingClass.registerBtn.setOnClickListener {
            checkInfo()
        }
    }

    private fun checkInfo() {
        if (bindingClass.firstName.text.isNotEmpty() &&
            bindingClass.lastName.text.isNotEmpty() &&
            bindingClass.email.text.isNotEmpty() &&
            bindingClass.phone.text.isNotEmpty() &&
            bindingClass.password.text.isNotEmpty() &&
            bindingClass.confPass.text.isNotEmpty()
        ) {
            checkEmail()
        } else {
            Toast.makeText(this@RegisterActivity, "Fill all fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkEmail() {
        val regex = "^[A-Za-z\\d+_.-]+@(.+)$"
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(bindingClass.email.text.toString())
        if (matcher.matches()) {
            checkPassword()
        } else {
            Toast.makeText(this@RegisterActivity, "Invalid email", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkPassword() {
        if (bindingClass.password.text.toString() == bindingClass.confPass.text.toString()) {
            val regex = "^(?=.*\\d)" +
                    "(?=.*[a-z])(?=.*[A-Z])" +
                    "(?=.*[@#$%^&+=])" +
                    "(?=\\S+$).{8,20}$"
            val p: Pattern = Pattern.compile(regex)
            val m: Matcher = p.matcher(bindingClass.password.text.toString())
            if (m.matches()) {
                val sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("password", bindingClass.password.text.toString()).apply()
                postData()

            } else {
                Toast.makeText(
                    this@RegisterActivity,
                    "Password must be contain Z-z, 1 capital letter and symbol",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(this@RegisterActivity, "Password must be same", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun postData() {
        val map = mapOf(
            "first_name" to bindingClass.firstName.text.toString(),
            "last_name" to bindingClass.lastName.text.toString(),
            "email" to bindingClass.email.text.toString(),
            "phone" to bindingClass.phone.text.toString()
        )
        api.postData(KEY, map).enqueue(object : Callback<UserDataModel> {
            @SuppressLint("CommitPrefEdits")
            override fun onResponse(call: Call<UserDataModel>, response: Response<UserDataModel>) {
                if (response.isSuccessful) {
                    val sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("token", response.body()?.token).apply()
                    createAccountPageActivity(map)

                } else {
                    println("Error")
                }
            }

            override fun onFailure(call: Call<UserDataModel>, t: Throwable) {
                println(t.message)
            }
        })
    }

    private fun createAccountPageActivity(map: Map<String, String>) {
        val intent = Intent(this@RegisterActivity, AccountPageActivity::class.java)
        intent.putExtra("first_name",map.get("first_name"))
        intent.putExtra("last_name",map.get("last_name"))
        intent.putExtra("email",map.get("email"))
        intent.putExtra("phone",map.get("phone"))
        startActivity(intent)

    }
}