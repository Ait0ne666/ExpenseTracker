package com.ait0ne.expensetracker.models.dto

import com.ait0ne.expensetracker.models.Currency
import java.util.*

data class MonthGraphRequest(val date: Date, val currency: Currency)