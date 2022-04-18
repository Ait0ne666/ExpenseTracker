package com.ait0ne.expensetracker.models.dto

import com.ait0ne.expensetracker.models.ExpenseDTO

data class SyncDTO (val expenses: List<ExpenseDTO>)