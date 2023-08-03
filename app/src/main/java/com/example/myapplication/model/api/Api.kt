package com.example.myapplication.model.api

import com.example.myapplication.data.ColorsGet
import com.example.myapplication.data.GetIconFromDirectory
import com.example.myapplication.data.UserDataInfo
import com.example.myapplication.data.UserDataModel
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface Api {
    companion object {
        private const val BASE_URL = "https://dev.apirequests.com/api/v2/"
        const val KEY = "098WXjnytf!rFN0lX7RinOA2hz@KkeDloMei2CRwRPXtzXPu1DMppkrGTqobF@w0"
        const val IMG_BASE_URL = "https://imsba.s3.amazonaws.com/"

        fun create(): Api {
            val retrofitBuilder =
                Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(this.BASE_URL)
                    .build()
            return retrofitBuilder.create(Api::class.java)
        }
    }

    @GET("website/settings")
    fun getColors(
        @Query("key") key: String
    ): Call<ColorsGet>

    @GET("website/settings")
    fun getIconMainPage(
        @Query("key") key: String
    ): Call<GetIconFromDirectory>

    @FormUrlEncoded
    @POST("users/register")
    fun postData(
        @Query("key") key: String, @FieldMap map: Map<String, String>
    ): Call<UserDataModel>

    @FormUrlEncoded
    @POST("users/login")
    fun loginUser(
        @Query("key") key: String,
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<UserDataModel>

    @GET("users/profile")
    fun checkTokenForLogin(
        @Query("key") key: String, @Header("token") token: String
    ): Call<UserDataInfo>

    @FormUrlEncoded
    @POST("users/profile")
    fun updateUserData(
        @Query("key") key: String,
        @Header("token") token: String,
        @FieldMap map: Map<String, String>
    ): Call<UserDataModel>

    @Multipart
    @POST("users/profile_photo")
    fun upload(
        @Query("key") key: String?, @Header("token") token: String?, @Part body: MultipartBody.Part
    ): Call<UserDataModel?>?
}