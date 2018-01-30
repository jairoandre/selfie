package com.japo.selfiepoc.client

import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitClient(private val baseUrl: String) {


    fun client(): Retrofit {
        val credentials = Credentials.basic("microservice", "access_key")
        val okHttpClient = OkHttpClient.Builder()
            .readTimeout(180, TimeUnit.SECONDS)
            .connectTimeout(180, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("Authorization", credentials)
                    .build()
                chain.proceed(requestBuilder)
            }.build()
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }
}
