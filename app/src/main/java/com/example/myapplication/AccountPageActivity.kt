package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.myapplication.PathUtil.RealPathUtil
import com.example.myapplication.Retrofit.RetrofitClient.IMG_BASE_URL
import com.example.myapplication.Retrofit.RetrofitClient.KEY
import com.example.myapplication.Retrofit.RetrofitClient.api
import com.example.myapplication.databinding.ActivityAccountPageBinding
import com.example.myapplication.model.UserDataModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part.Companion.createFormData
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException

class AccountPageActivity : AppCompatActivity() {
    private lateinit var bindingClass: ActivityAccountPageBinding
    private lateinit var email: String
    private var token: String? = null
    private var image: String? = null
    var uri: Uri? = null
    var path: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingClass = ActivityAccountPageBinding.inflate(layoutInflater)
        setContentView(bindingClass.root)
        getUserInfoFromActivity()
        val sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE)
        token = sharedPreferences.getString("token", null).toString()
        makeEditTextChangeable(false)
        bindingClass.changeUserDataBtn.setOnClickListener {
            openChangingLayout(true)
        }
        bindingClass.cancelChangesUserDataBtn.setOnClickListener {
            cancelChanges()
        }
        bindingClass.submitBtn.setOnClickListener {
            checkPassword()
        }
        bindingClass.imgLoaderLayout.setOnClickListener {
            loadImage()
        }
        bindingClass.savePng.setOnClickListener {
            uploadImage()
        }
    }

    private fun makeEditTextChangeable(isClicked: Boolean) {
        bindingClass.firstName.isFocusableInTouchMode = isClicked
        bindingClass.lastName.isFocusableInTouchMode = isClicked
        bindingClass.email.isFocusableInTouchMode = isClicked
        bindingClass.phone.isFocusableInTouchMode = isClicked
        if (!isClicked) {
            bindingClass.firstName.clearFocus()
            bindingClass.lastName.clearFocus()
            bindingClass.email.clearFocus()
            bindingClass.phone.clearFocus()
        }
    }


    @SuppressLint("SetTextI18n")
    private fun getUserInfoFromActivity() {
        bindingClass.firstName.setText(intent.getStringExtra("first_name"))
        bindingClass.lastName.setText(intent.getStringExtra("last_name"))
        bindingClass.phone.setText(intent.getStringExtra("phone"))
        email = intent.getStringExtra("email").toString()
        bindingClass.email.setText(email)
        bindingClass.changeUserDataBtn.setText("Change")
        bindingClass.savePng.isVisible = false
        image = intent.getStringExtra("image")
        if (!image.isNullOrEmpty()){
        Glide.with(this@AccountPageActivity)
            .load(IMG_BASE_URL + image)
            .into(bindingClass.accountImg)
        }
    }

    private fun openChangingLayout(isClicked: Boolean) {
        bindingClass.cancelChangesUserDataBtn.isVisible = isClicked
        bindingClass.editLayout.isVisible = isClicked
        makeEditTextChangeable(isClicked)
    }

    private fun cancelChanges() {
        bindingClass.cancelChangesUserDataBtn.isVisible = false
        bindingClass.editLayout.isVisible = false
        makeEditTextChangeable(false)
        getUserInfoFromActivity()
    }

    private fun checkPassword() {
        if (bindingClass.submitEdit.text.isNullOrEmpty()) {
            Toast.makeText(this@AccountPageActivity, "Write Your Password", Toast.LENGTH_SHORT)
                .show()
        } else {
            val password: String = bindingClass.submitEdit.text.toString()
            api.loginUser(KEY, email.toString(), password.toString())
                .enqueue(object : Callback<UserDataModel> {
                    override fun onResponse(
                        call: Call<UserDataModel>, response: Response<UserDataModel>
                    ) {
                        if (response.isSuccessful) {
                            if (response.body()?.token != null) {
                                updateData(token!!)
                            } else println("Something incorrect")
                        } else Toast.makeText(
                            this@AccountPageActivity, "Incorrect password", Toast.LENGTH_SHORT).show()
                    }
                    override fun onFailure(call: Call<UserDataModel>, t: Throwable) {
                        println(t.message)
                    }
                })
        }
    }

    private fun updateData(token: String) {
        val map = mapOf(
            "first_name" to bindingClass.firstName.text.toString(),
            "last_name" to bindingClass.lastName.text.toString(),
            "email" to bindingClass.email.text.toString(),
            "username" to bindingClass.email.text.toString(),
            "phone" to bindingClass.phone.text.toString()
        )
        api.updateUserData(KEY, token, map).enqueue(object : Callback<UserDataModel> {
            override fun onResponse(call: Call<UserDataModel>, response: Response<UserDataModel>) {
                if (response.isSuccessful) {
                    setData(
                        response.body()!!.first_name,
                        response.body()!!.last_name,
                        response.body()!!.email,
                        response.body()!!.phone
                    )
                    Toast.makeText(this@AccountPageActivity, "Data is loaded", Toast.LENGTH_SHORT)
                        .show()
                } else println("ERROR")
            }

            override fun onFailure(call: Call<UserDataModel>, t: Throwable) {
                println(t.message)
            }
        })
    }

    private fun setData(firstName: String, lastName: String, email: String, phone: String) {
        bindingClass.firstName.setText(firstName)
        bindingClass.lastName.setText(lastName)
        bindingClass.email.setText(email)
        bindingClass.phone.setText(phone)
        openChangingLayout(false)
    }

    private fun loadImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        launcher.launch(intent)
    }

    var launcher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            uri = data!!.data
            val context: Context = this@AccountPageActivity
            path = RealPathUtil.getRealPath(context, uri)
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                bindingClass.accountImg.setImageBitmap(bitmap)
                bindingClass.savePng.setVisibility(View.VISIBLE)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == 100) {
                bindingClass.accountImg.setImageURI(data!!.data)
            }
        }
    }

    private fun uploadImage() {
        val f = File(path)
        val reqFile =
            RequestBody.create(contentResolver.getType(uri!!)?.let { it.toMediaTypeOrNull() }, f)
        val body: MultipartBody.Part = createFormData("file", f.name, reqFile)
        val call = api.upload(KEY, token, body)
        call!!.enqueue(object : Callback<UserDataModel?> {
            override fun onResponse(
                call: Call<UserDataModel?>, response: Response<UserDataModel?>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@AccountPageActivity, "Image Successfully added", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        this@AccountPageActivity, "Something gone incorrect", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserDataModel?>, t: Throwable) {
                println(t.message)
            }
        })
    }
}

