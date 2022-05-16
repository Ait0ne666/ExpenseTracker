package com.ait0ne.expensetracker.models

import java.io.Serializable

data class CurrencyRates(
    val usd: Float,
    val eur: Float,
    val tbh: Float,
) : Serializable