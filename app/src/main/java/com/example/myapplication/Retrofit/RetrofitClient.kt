package com.example.myapplication.Retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object RetrofitClient {
    const val BASE_URL = "https://dev.apirequests.com/api/v2/"
    const val KEY = "098WXjnytf!rFN0lX7RinOA2hz@KkeDloMei2CRwRPXtzXPu1DMppkrGTqobF@w0"
    const val IMG_BASE_URL = "https://imsba.s3.amazonaws.com/"

    val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()


    val api: Api = retrofitBuilder.create(Api::class.java)
    }
