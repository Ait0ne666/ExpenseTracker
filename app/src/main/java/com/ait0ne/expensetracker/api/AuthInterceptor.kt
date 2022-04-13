package com.ait0ne.expensetracker.api

import com.ait0ne.expensetracker.utils.Constants
import okhttp3.Interceptor
import okhttp3.Response




class AuthInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response = chain.run {
        proceed(
            request()
                .newBuilder()
                .addHeader("Authorization", "Bearer " + Constants.API_TOKEN)
                .build()
        )
    }
}