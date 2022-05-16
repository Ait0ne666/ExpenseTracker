package com.ait0ne.expensetracker.api

import android.content.SharedPreferences
import android.util.Log
import com.ait0ne.expensetracker.utils.Constants
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.Response


class AuthInterceptor(val sharedPreferences: SharedPreferences) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response = chain.run {
        val token = sharedPreferences.getString("jwt", "")



        proceed(
            request()
                .newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        )
    }
}


class Logger() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response = chain.run {

        val builder = GsonBuilder()
        val gson = builder.create()
        chain?.request().let {

            Log.i("Retrofit", gson.toJson(it))
        }
        Log.i("Retrofit", "log")
        proceed(
            chain.request()
        )
    }
}