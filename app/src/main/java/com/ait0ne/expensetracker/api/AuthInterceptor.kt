package com.ait0ne.expensetracker.api

import android.content.SharedPreferences
import com.ait0ne.expensetracker.utils.Constants
import okhttp3.Interceptor
import okhttp3.Response




class AuthInterceptor(val sharedPreferences: SharedPreferences): Interceptor {
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