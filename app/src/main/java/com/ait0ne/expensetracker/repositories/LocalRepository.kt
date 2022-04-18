package com.ait0ne.expensetracker.repositories

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import com.ait0ne.expensetracker.models.Currency

class LocalRepository(private val sharedPreferences: SharedPreferences) {
    val currency: MutableLiveData<Currency> = MutableLiveData(getCurrency())


    fun getCurrency(): Currency {
        val value = sharedPreferences.getString("currency", "tbh")

        when (value) {
            "tbh" -> {
                return Currency.THB
            }
            "usd" -> {
                return Currency.USD
            }
            "eur" -> {
                return Currency.EUR
            }
            "rub" -> {
                return Currency.RUB
            }
            else -> {
                return Currency.THB
            }
        }

    }


    fun putCurrency(cur: Currency) {
        sharedPreferences.edit().putString("currency", cur.text).apply()
        currency.postValue(cur)
    }


}