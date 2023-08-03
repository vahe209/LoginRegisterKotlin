package com.example.myapplication.view

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.myapplication.pathUtil.RealPathUtil
import com.example.myapplication.viewModel.AccountPageActivityVM
import com.example.myapplication.databinding.ActivityAccountPageBinding
import com.example.myapplication.model.api.Api.Companion.IMG_BASE_URL
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part.Companion.createFormData
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException

class AccountPageActivity : AppCompatActivity() {
    private lateinit var bindingClass: ActivityAccountPageBinding
    private lateinit var accountPageActivityVM: AccountPageActivityVM
    private lateinit var email: String
    private var image: String? = null
    private var uri: Uri? = null
    private lateinit var map: HashMap<String, String>
    var path: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingClass = ActivityAccountPageBinding.inflate(layoutInflater)
        setContentView(bindingClass.root)
        accountPageActivityVM = ViewModelProvider(this)[AccountPageActivityVM::class.java]
        getUserInfoFromActivity()
        makeEditTextChangeable(false)
        bindingClass.changeUserDataBtn.setOnClickListener { openChangingLayout(true) }
        bindingClass.cancelChangesUserDataBtn.setOnClickListener { cancelChanges() }
        bindingClass.submitBtn.setOnClickListener {
            map = mapOf(
                "first_name" to bindingClass.firstName.text.toString(),
                "last_name" to bindingClass.lastName.text.toString(),
                "email" to bindingClass.email.text.toString(),
                "username" to bindingClass.email.text.toString(),
                "phone" to bindingClass.phone.text.toString()
            ) as HashMap<String, String>
            accountPageActivityVM.checkPassword(
                bindingClass.email.text.toString(),
                bindingClass.submitEdit.text.toString(),
                map,
                this
            )
            accountPageActivityVM.isCorrectPassword.observe(this) {
                if (it) {
                    setData(map)
                }
            }
        }
        bindingClass.imgLoaderLayout.setOnClickListener { loadImage() }
        bindingClass.savePng.setOnClickListener { uploadImage() }
        bindingClass.logOut.setOnClickListener { logOut() }
    }

    private fun logOut() {
        accountPageActivityVM.logOut(this)
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
        bindingClass.changeUserDataBtn.text = "Change"
        bindingClass.savePng.isVisible = false
        image = intent.getStringExtra("image")
        if (!image.isNullOrEmpty()) {
            Glide.with(this@AccountPageActivity).load(IMG_BASE_URL + image)
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

    private fun setData(map: HashMap<String, String>) {
        bindingClass.firstName.setText(map["first_name"])
        bindingClass.lastName.setText(map["last_name"])
        bindingClass.email.setText(email)
        bindingClass.phone.setText(map["phone"])
        openChangingLayout(false)
    }

    private fun loadImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        launcher.launch(intent)
    }

    private var launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            uri = data!!.data
            path = RealPathUtil.getRealPath(this, uri)
            try {
                @Suppress("DEPRECATION") val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                bindingClass.accountImg.setImageBitmap(bitmap)
                bindingClass.savePng.isVisible = true
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == 100) {
                bindingClass.accountImg.setImageURI(data!!.data)
            }
        }
    }

    private fun uploadImage() {
        val f = File(path!!)
        val reqFile = f.asRequestBody(contentResolver.getType(uri!!)?.toMediaTypeOrNull())
        val body: MultipartBody.Part = createFormData("file", f.name, reqFile)
        accountPageActivityVM.uploadImage(body, this)
    }
}