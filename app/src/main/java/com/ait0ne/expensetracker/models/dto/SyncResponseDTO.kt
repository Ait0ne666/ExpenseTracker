package com.ait0ne.expensetracker.models.dto

import com.ait0ne.expensetracker.models.CurrencyRates
import com.ait0ne.expensetracker.models.ExpenseFromApi
import java.util.*



data class SyncResponseDTO (val expenses: List<ExpenseFromApi>, val rates: CurrencyRates)