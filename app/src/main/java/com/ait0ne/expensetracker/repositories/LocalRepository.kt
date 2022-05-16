package com.ait0ne.expensetracker.repositories

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import com.ait0ne.expensetracker.models.Currency
import com.ait0ne.expensetracker.models.CurrencyRates
import com.ait0ne.expensetracker.utils.DateUtils
import com.google.gson.GsonBuilder
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.util.*

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


    fun putLastSync(lastSync: Date) {
        val dateString = com.ait0ne.expensetracker.utils.DateUtils.isoFormatter.format(lastSync)

        sharedPreferences.edit().putString("lastSync", dateString).apply()
    }


    fun getLastSync(): Date? {
        val lastSync = sharedPreferences.getString("lastSync", null)

        lastSync?.let {

            val date = com.ait0ne.expensetracker.utils.DateUtils.isoFormatter.parse(lastSync)


            return DateUtils.localToUTC(date)
        }


        return null
    }


    fun putRates(rates: CurrencyRates) {
        val builder = GsonBuilder()
        val gson = builder.create()

        val json = gson.toJson(rates)


        sharedPreferences.edit().putString("rates", json).apply()
    }

    fun getRates(): CurrencyRates {
        val json = sharedPreferences.getString("rates", null)

        json?.let {
            val builder = GsonBuilder()
            val gson = builder.create()


            return gson.fromJson(it, CurrencyRates::class.java)
        }


        return CurrencyRates(1F, 1F, 1F)

    }


}