package com.example.elocker.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "https://esewadev.punjab.gov.in/common/api/"
    private const val BASE_URL_2 = "https://devmsewa.psegs.in/m-sewa/api/"
    private  const val BASE_URL_3 = "https://esewa.punjab.gov.in/common/api/"


    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }



    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    val api2: ApiService2 by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_2)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService2::class.java)
    }

    val api3: ApiService3 by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_3)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService3::class.java)
    }

}

