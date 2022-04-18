package com.ait0ne.expensetracker.models.dto

import com.ait0ne.expensetracker.models.Currency
import java.io.Serializable
import java.util.*

data class CreateExpenseRequest (
    val category_name: String,
    val amount: Float,
    val date: Date,
    val currency: Currency,
    val title: String?,
    val id: String?
        ): Serializable