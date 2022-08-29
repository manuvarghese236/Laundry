package com.blueskyprojects.eisa.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://blueskyprojects.me/laundry/"
class RfClient {
    companion object {
        fun create(): EndPoints {
            val logging = HttpLoggingInterceptor()
// set your desired log level
// set your desired log level
            logging.level = HttpLoggingInterceptor.Level.BODY

            val httpClient = OkHttpClient.Builder()
// add your other interceptors …

// add logging as last interceptor
// add your other interceptors …

// add logging as last interceptor
            httpClient.addInterceptor(logging)
            val retrofit = Retrofit.Builder()
                .addConverterFactory(
                    GsonConverterFactory.create()
                )
                .baseUrl(BASE_URL)
                .client(httpClient.build())
                .build()

            return retrofit.create(EndPoints::class.java)
        }
    }
}