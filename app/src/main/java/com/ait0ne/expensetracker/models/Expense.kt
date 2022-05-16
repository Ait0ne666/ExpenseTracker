package com.ait0ne.expensetracker.models

import android.content.ContentValues
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
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
    var category_id: Int,
    var date: Date,
    var amount: Float,
    var currency: Currency,
    var cloud_id: String?,
    var created_at: Date,
    var updated_at: Date,
    var deleted_at: Date?,
    var dirty: Boolean



    ) : Serializable {
    companion object {
        fun fromContentValues(values: ContentValues?): Expense? {

            var id: Int? = null
            var title: String? = null
            var categoryID: Int? = null
            var date: Date? = null
            var amount: Float? = null
            var currency: Currency? = null
            var cloud_id: String? = null
            var createdAt: Date? = null
            var deletedAt: Date? = null
            var updatedAt: Date? = null
            var dirty: Boolean = false

            values?.let {
                if (it.containsKey("id")) {
                    id = it.getAsInteger("id")
                }
                if (it.containsKey("title")) {
                    title = it.getAsString("title")
                }

                if (it.containsKey("category_id")) {
                    categoryID = it.getAsInteger("category_id")
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

                    date = Date(it.getAsLong("date"))
                }

                if (it.containsKey("created_at")) {

                    createdAt = Date(it.getAsLong("created_at"))
                }

                if (it.containsKey("updated_at")) {

                    updatedAt = Date(it.getAsLong("updated_at"))
                }

                if (it.containsKey("deleted_at")) {

                    deletedAt = Date(it.getAsLong("deleted_at"))
                }

                if (it.containsKey("dirty")) {

                    dirty = it.getAsBoolean("dirty")
                }


            }


            if (  amount != null && categoryID != null && date != null && currency != null && createdAt != null && updatedAt != null) {

                return Expense(id, title, categoryID!!, date!!, amount!!, currency!!, cloud_id, createdAt!!, updatedAt!!, deletedAt, dirty)
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
                put("date", expense.date.time)
                put("created_at", expense.created_at.time)
                put("updated_at", expense.updated_at.time)
                put("currency", expense.currency.text)
                put("dirty", expense.dirty)
                expense.deleted_at?.let {
                    put("deleted_at", it.time)
                }
                expense.cloud_id?.let {
                    put("cloud_id", expense.cloud_id)
                }

            }


            return values
        }

    }
}


data class ExpenseDTO(
    var id: Int,
    var title: String?,
    var category: Category,
    var date: Date,
    var amount: Float,
    var category_id: Int,
    var currency: Currency
) : Serializable



data class ExpenseWithCategory(
    @Embedded val expense: Expense,
    @Relation(
        parentColumn = "category_id",
        entityColumn = "id"
    )
    val category: Category
): Serializable



data class ExpenseFromApi(
    val id: Int?,
    val cloud_id: String?,
    val title: String?,
    val date: String,
    val category_name: String,
    val category_id: String?,
    val amount: Float,
    val currency: Currency,
    val created_at: String,
    val updated_at: String,
    val deleted_at: String?,
    val success: Boolean?


)