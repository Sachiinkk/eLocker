//package com.example.elocker.data.remote
//
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//
//object RetrofitInstance {
//    private const val BASE_URL = "https://esewadev.punjab.gov.in/common/api/"
//    // Replace with actual base URL
//    private const val BASE_URL_2 = "https://devmsewa.psegs.in/m-sewa/api/"
//    val api: ApiService by lazy {
//        Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(ApiService::class.java)
//    }
//    val api2: ApiService2 by lazy {
//        Retrofit.Builder()
//            .baseUrl(BASE_URL_2)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(ApiService2::class.java)
//    }
//
//}


package com.example.elocker.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "https://esewadev.punjab.gov.in/common/api/" // Replace with actual
    private const val BASE_URL_2 = "https://devmsewa.psegs.in/m-sewa/api/" // Replace with actual

//    https://devmsewa.psegs.in/m-sewa/api/common/v1/Fetch-elocker
//    https://devmsewa.psegs.in/m-sewa/api/
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
}

