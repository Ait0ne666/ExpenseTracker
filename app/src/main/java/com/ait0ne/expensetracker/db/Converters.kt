package com.ait0ne.expensetracker.db

import androidx.room.TypeConverter
import com.ait0ne.expensetracker.models.Currency
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class Converters {

    @TypeConverter
    fun toDate(dateLong: Long?): Date? {
        return dateLong?.let { Date(it) }
    }

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }



    @TypeConverter
    fun toCurrency(value: String): Currency {
        return when(value) {
            "THB" -> {
                Currency.THB
            }
            "USD" -> {
                Currency.USD
            }
            "RUB" -> {
                Currency.RUB
            }
            else -> {
                Currency.RUB
            }
        }
    }

    @TypeConverter
    fun fromCurrency(currency: Currency): String {
        return currency.text
    }
}