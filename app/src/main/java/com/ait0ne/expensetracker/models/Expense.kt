package com.ait0ne.expensetracker.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*




enum class Currency(val text: String) {
    THB("THB"),
    USD("USD"),
    RUB("RUB")
}



@Entity(
    tableName = "expenses"
)
data class Expense (
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    var title: String,
    var category: String,
    var createdAt: Date,
    var amount: Float,
    var currency: Currency,
    var amountRub: Float
        ): Serializable



data class ExpenseDTO (
    var id: String,
    var title: String?,
    var category: Category,
    var date: Date,
    var amount: Float,
    var category_id: String
): Serializable