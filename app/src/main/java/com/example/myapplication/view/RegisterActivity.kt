package com.example.myapplication.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.viewModel.RegisterActivityVM
import com.example.myapplication.databinding.ActivityRegisterBinding
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {
    private lateinit var bindingClass: ActivityRegisterBinding
    private var registerActivityVM = RegisterActivityVM()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerActivityVM = ViewModelProvider(this)[RegisterActivityVM::class.java]
        bindingClass = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(bindingClass.root)
        bindingClass.registerBtn.setOnClickListener {
            checkInfo()
        }
        bindingClass.loginBtn.setOnClickListener {
            finish()
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
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkEmail() {
        registerActivityVM.checkEmail(bindingClass.email.text.toString(),this)
        registerActivityVM.isValidEmail.observe(this) {
            if (it) {
                checkPassword()
            }
        }

    }

    private fun checkPassword() {
        if (bindingClass.password.text.toString() == bindingClass.confPass.text.toString()) {
            registerActivityVM.checkPassword(bindingClass.password.text.toString(), this)
            registerActivityVM.isValidPassword.observe(this, Observer {
                if (it) {
                    postData()
                }
            })
        } else {
            Toast.makeText(this, "Password must be same", Toast.LENGTH_SHORT).show()
        }
    }

    private fun postData() {
        val map = mapOf(
            "first_name" to bindingClass.firstName.text.toString(),
            "last_name" to bindingClass.lastName.text.toString(),
            "password" to bindingClass.password.text.toString(),
            "email" to bindingClass.email.text.toString(),
            "phone" to bindingClass.phone.text.toString(),
        )
        registerActivityVM.apply {
           this.map.putAll(map)
            post(this@RegisterActivity)
        }
    }

}