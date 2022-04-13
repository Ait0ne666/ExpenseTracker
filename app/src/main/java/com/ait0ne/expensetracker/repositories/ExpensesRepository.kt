package com.ait0ne.expensetracker.repositories

import com.ait0ne.expensetracker.api.RetrofitInstance
import com.ait0ne.expensetracker.db.ExpenseDB
import com.ait0ne.expensetracker.models.Expense
import com.ait0ne.expensetracker.models.dto.*
import retrofit2.Response
import java.util.*

class ExpensesRepository(
    private val db: ExpenseDB
) {


    suspend fun upsert(expense: Expense) = db.getExpenseDao().upsert(expense)


    fun getExpenses() = db.getExpenseDao().getAllExpenses()

    suspend fun deleteExpense(expense: Expense) = db.getExpenseDao().deleteExpense(expense)


    suspend fun createExpense(request: CreateExpenseRequest) =
        RetrofitInstance.api.createExpense(request)


    suspend fun dayExpenses(request: DayExpensesRequest) =
        RetrofitInstance.api.dayExpenses(request)


    suspend fun monthExpenses(date: Date, category: String?): Response<MonthExpensesDTO> {
        val request = MonthExpensesRequest(date, category)

        return RetrofitInstance.api.monthExpenses(request)
    }

    suspend fun deleteExpense(id: String) =
        RetrofitInstance.api.deleteExpense(id)
}