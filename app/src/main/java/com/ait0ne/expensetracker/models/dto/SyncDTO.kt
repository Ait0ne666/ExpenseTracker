package com.ait0ne.expensetracker.models.dto


import com.ait0ne.expensetracker.models.ExpenseFromApi
import java.util.*

data class SyncDTO (val expenses: List<ExpenseFromApi>, val last_sync: String?)