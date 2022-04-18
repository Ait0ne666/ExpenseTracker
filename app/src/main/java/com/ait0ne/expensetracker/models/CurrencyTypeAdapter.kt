package com.ait0ne.expensetracker.models

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.lang.Exception

class CurrencyTypeAdapter : TypeAdapter<Currency>() {

    override fun write(out: JsonWriter?, value: Currency?) {

        value?.let {
            out?.value(value.text)
        }


    }

    override fun read(reader: JsonReader?): Currency {
        reader?.let {
            if (reader.peek() === JsonToken.NULL) {
                reader.nextNull()
                throw Exception()
            }
            val value = reader.nextString()


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
                    throw Exception()
                }
            }

        }

        throw Exception()
    }


}