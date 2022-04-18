package com.ait0ne.expensetracker.models.dto

data class MonthGraphDTO(val month_total: Float, val categories: List<MonthCategory>)


data class MonthCategory(val category_id: String, val category_title: String, val amount: Float)