package com.ait0ne.expensetracker.api

import android.content.SharedPreferences
import com.ait0ne.expensetracker.models.Currency
import com.ait0ne.expensetracker.models.CurrencyTypeAdapter
import com.ait0ne.expensetracker.utils.Constants
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DateFormat

class RetrofitInstance(val sharedPreferences: SharedPreferences) {

    private var _instance: Retrofit? = null
    val instance: Retrofit
        get() {
            if (_instance != null) {
                return _instance as Retrofit
            } else {
                _instance = buildRetrofit()
                return _instance as Retrofit
            }
        }


    val api: ExpensesAPI by lazy {
            instance.create(ExpensesAPI::class.java)
    }




    private fun buildRetrofit():Retrofit {


        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)




        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(AuthInterceptor(sharedPreferences))
            .build()

        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").registerTypeAdapter(Currency::class.java, CurrencyTypeAdapter()).create()

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)

            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()


        return retrofit
    }






//
//    companion object {
//
//
//
//
//        private val retrofit by lazy {
//            val logging = HttpLoggingInterceptor()
//            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
//
//
//
//
//            val client = OkHttpClient.Builder()
//                .addInterceptor(logging)
//                .addInterceptor(AuthInterceptor())
//                .build()
//
//            val gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").registerTypeAdapter(Currency::class.java, CurrencyTypeAdapter()).create()
//
//            Retrofit.Builder()
//                .baseUrl(Constants.BASE_URL)
//
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .client(client)
//                .build()
//        }
//
//
//        val api by lazy {
//            retrofit.create(ExpensesAPI::class.java)
//        }
//    }
}