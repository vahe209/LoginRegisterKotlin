package com.example.myapplication.Retrofit

import com.example.myapplication.Colors.ColorsGet
import com.example.myapplication.Icons.GetIconFromDirectory
import com.example.myapplication.model.UserDataInfo
import com.example.myapplication.model.UserDataModel
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface Api {

    @GET("website/settings")
    fun getColors(
        @Query("key") key:String) : Call<ColorsGet>

    @GET("website/settings")
    fun getIconMainPage(
        @Query("key") key:String):Call <GetIconFromDirectory>

    @FormUrlEncoded
    @POST("users/register")
    fun postData(
        @Query("key") key:String,
        @FieldMap map: Map<String, String>
    ):Call<UserDataModel>

    @FormUrlEncoded
    @POST("users/login")
    fun loginUser(
        @Query("key") key: String,
        @Field("username") username: String,
        @Field("password") password: String
    ):Call<UserDataModel>

   @GET("users/profile")
    fun checkTokenForLogin(
        @Query("key")key : String,
        @Header("token")token: String
    ):Call<UserDataInfo>

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
        @Query("key") key: String?,
        @Header("token") token: String?,
        @Part image: MultipartBody.Part
    ): Call<UserDataModel?>?


}