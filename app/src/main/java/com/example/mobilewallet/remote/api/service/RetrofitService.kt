package com.example.mobilewallet.remote.api.service

import com.example.mobilewallet.DateDeserializer
import com.example.mobilewallet.remote.api.WalletApi
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitService @Inject constructor() {
    private val baseUrl = "http://10.0.2.2:8092/springboot-rest-api/" // For emulator

    private val gson = GsonBuilder()
        .registerTypeAdapter(Date::class.java, DateDeserializer())
        .create()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val walletApi: WalletApi = retrofit.create(WalletApi::class.java)
}