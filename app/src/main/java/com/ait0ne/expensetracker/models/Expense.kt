package com.ait0ne.expensetracker.models

import android.content.ContentValues
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ait0ne.expensetracker.utils.DateUtils
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*


enum class Currency(val text: String) {
    THB("tbh"),
    USD("usd"),
    RUB("rub"),
    EUR("eur");


    companion object {
        fun symbol(cur: Currency): String {

            when (cur) {
                THB -> {
                    return "฿"
                }
                USD -> {
                    return "$"
                }
                RUB -> {
                    return "₽"
                }
                EUR -> {
                    return "€"
                }
            }
        }

        fun fromText(str: String): Currency {
            return when (str) {
                "tbh" -> {
                    THB
                }
                "usd" -> {
                    USD
                }
                "eur" -> {
                    EUR
                }
                "rub" -> {
                    RUB
                }
                else -> {
                    RUB
                }
            }
        }

    }
}


@Entity(
    tableName = "expenses"
)
data class Expense(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    var title: String?,
    var category_id: String,
    var date: Date,
    var amount: Float,
    var currency: Currency,
    var cloud_id: String?



    ) : Serializable {
    companion object {
        fun fromContentValues(values: ContentValues?): Expense? {

            var id: Int? = null
            var title: String? = null
            var categoryID: String? = null
            var date: Date? = null
            var amount: Float? = null
            var currency: Currency? = null
            var cloud_id: String? = null

            values?.let {
                if (it.containsKey("id")) {
                    id = it.getAsInteger("id")
                }
                if (it.containsKey("title")) {
                    title = it.getAsString("title")
                }

                if (it.containsKey("category_id")) {
                    categoryID = it.getAsString("category_id")
                }

                if (it.containsKey("currency")) {
                    currency = Currency.fromText(it.getAsString("currency"))
                }


                if (it.containsKey("amount")) {
                    amount = it.getAsFloat("amount")
                }

                if (it.containsKey("cloud_id")) {
                    cloud_id = it.getAsString("cloud_id")
                }

                if (it.containsKey("date")) {

                    date = DateUtils.isoFormatter.parse(it.getAsString("date"))
                }

            }


            if (  amount != null && categoryID != null && date != null && currency != null) {

                return Expense(id, title, categoryID!!, date!!, amount!!, currency!!, cloud_id)
            }


            return null


        }


        fun toContentValues(expense: Expense): ContentValues {
            val values = ContentValues().apply {
                expense.id?.let {
                    put("id", it)
                }

                put("title", expense.title)
                put("amount", expense.amount)
                put("category_id", expense.category_id)
                put("date", DateUtils.isoFormatter.format(expense.date))
                put("currency", expense.currency.text)
                expense.cloud_id?.let {
                    put("cloud_id", expense.cloud_id)
                }

            }


            return values
        }

    }
}


data class ExpenseDTO(
    var id: String,
    var title: String?,
    var category: Category,
    var date: Date,
    var amount: Float,
    var category_id: String,
    var currency: Currency
) : Serializable