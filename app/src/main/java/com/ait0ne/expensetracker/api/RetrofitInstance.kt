package com.ait0ne.expensetracker.api

import com.ait0ne.expensetracker.utils.Constants
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DateFormat

class RetrofitInstance {
    companion object {
        private val retrofit by lazy {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(AuthInterceptor())
                .build()

            val gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create()

            Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)

                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build()
        }


        val api by lazy {
            retrofit.create(ExpensesAPI::class.java)
        }
    }
}