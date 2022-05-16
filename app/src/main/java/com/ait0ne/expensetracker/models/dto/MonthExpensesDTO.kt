package com.ait0ne.expensetracker.models.dto


import com.ait0ne.expensetracker.models.ExpenseWithCategory

data class MonthExpensesDTO (val month_total: Float, val expenses: ArrayList<ExpenseWithCategory>)