package com.example.myapplication.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.myapplication.viewModel.LoginActivityVM
import com.example.myapplication.databinding.ActivityMainBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var bindingClass: ActivityMainBinding
    private lateinit var loginActivityVM: LoginActivityVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingClass = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindingClass.root)
        loginActivityVM = ViewModelProvider(this)[LoginActivityVM::class.java]
        loginActivityVM.getBackgroundColorAndIcon()
        loginActivityVM.bgColor.observe(this) {
            bindingClass.mainActivityConstraint.setBackgroundColor(it.toColorInt())
        }
        loginActivityVM.icon.observe(this) {
            Glide.with(this).load(it).into(bindingClass.icon)
            bindingClass.mainActivityConstraint.isVisible = true
        }
        loginActivityVM.checkToken(this)
        bindingClass.registerBtn.setOnClickListener { createRegisterActivity() }
        bindingClass.loginBtn.setOnClickListener {
            if (!bindingClass.emailEdText.text.isNullOrEmpty() && !bindingClass.passwordEdText.text.isNullOrEmpty()) {
                loginActivityVM.checkData(
                    bindingClass.emailEdText.text.toString(),
                    bindingClass.passwordEdText.text.toString(),
                    this)
            } else {
                Toast.makeText(this, "Fill All Fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createRegisterActivity() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}



