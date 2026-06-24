package com.example.data.api

import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // Default to the Loopback IP pointing to the Express server running on the developer host machine
    private const val DEFAULT_BASE_URL = "http://10.0.2.2:3000/"

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .build()
    }

    val api: BackendApi by lazy {
        // Resolve backend URL from BuildConfig if configured via secrets/.env, otherwise use loopback fallback
        val baseUrl = try {
            val configUrl = BuildConfig.BACKEND_API_URL
            if (!configUrl.isNullOrEmpty()) {
                if (configUrl.endsWith("/")) configUrl else "$configUrl/"
            } else {
                DEFAULT_BASE_URL
            }
        } catch (e: Exception) {
            DEFAULT_BASE_URL
        }

        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(BackendApi::class.java)
    }
}
