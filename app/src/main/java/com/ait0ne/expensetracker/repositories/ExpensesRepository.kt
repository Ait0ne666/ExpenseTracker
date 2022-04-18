package com.ait0ne.expensetracker.repositories

import android.content.ContentProvider
import android.content.ContentResolver
import android.content.ContentValues
import android.os.Bundle
import androidx.core.database.getFloatOrNull
import androidx.core.database.getStringOrNull
import com.ait0ne.expensetracker.api.RetrofitInstance
import com.ait0ne.expensetracker.db.ExpenseDB
import com.ait0ne.expensetracker.models.Currency
import com.ait0ne.expensetracker.models.Expense
import com.ait0ne.expensetracker.models.dto.*
import com.ait0ne.expensetracker.providers.AppContentProvider
import com.ait0ne.expensetracker.utils.DateUtils
import retrofit2.Response
import retrofit2.Retrofit
import java.util.*

class ExpensesRepository(
    private val db: ExpenseDB,
    private val RetrofitInstance: RetrofitInstance,
    private val contentResolver: ContentResolver
) {


    fun createExpenseLocal(createExpenseRequest: CreateExpenseRequest): Expense? {
        var expense = Expense(
            title = createExpenseRequest.title,
            amount = createExpenseRequest.amount,
            category_id = createExpenseRequest.category_name,
            currency = createExpenseRequest.currency,
            date = createExpenseRequest.date,
            cloud_id = null,
            id = null
        )


        val uri = contentResolver.insert(AppContentProvider.URI, Expense.toContentValues(expense))

        uri?.let {
            val id = uri.lastPathSegment
            return expense.copy(id = id?.toInt())
        }


        return null

    }


    fun getExpensesLocal(): MutableList<Expense> {
        val cursor = contentResolver.query(AppContentProvider.URI, arrayOf(), null, null, null)

        val expenses = mutableListOf<Expense>()

        cursor?.let {

            it.moveToFirst()

            while (true) {
                if (it.isAfterLast) {
                    break
                }
                val id = it.getStringOrNull(it.getColumnIndex("id"))
                val title = it.getStringOrNull(it.getColumnIndex("title"))
                val amount = it.getFloatOrNull(it.getColumnIndex("amount"))
                val categoryId = it.getStringOrNull(it.getColumnIndex("category_id"))
                val date = it.getStringOrNull(it.getColumnIndex("date"))
                val cloudID = it.getStringOrNull(it.getColumnIndex("cloud_id"))
                val currency = it.getStringOrNull(it.getColumnIndex("currency"))

                if (categoryId != null && date != null && currency != null && amount != null) {
                    expenses.add(
                        Expense(
                            id = id?.toInt(),
                            title = title,
                            category_id = categoryId,
                            amount = amount,
                            date = DateUtils.isoFormatter.parse(date),
                            currency = Currency.fromText(currency),
                            cloud_id = cloudID


                        )
                    )

                }


                it.moveToNext()

            }
        }


        return expenses
    }


    fun getExpenses() = db.getExpenseDao().getAllExpenses()

    suspend fun deleteExpense(expense: Expense) = db.getExpenseDao().deleteExpense(expense)


    suspend fun createExpense(request: CreateExpenseRequest) =
        RetrofitInstance.api.createExpense(request)


    suspend fun dayExpenses(request: DayExpensesRequest) =
        RetrofitInstance.api.dayExpenses(request)


    suspend fun monthExpenses(
        date: Date,
        currency: Currency,
        category: String?
    ): Response<MonthExpensesDTO> {
        val request = MonthExpensesRequest(date, currency, category)

        return RetrofitInstance.api.monthExpenses(request)
    }

    suspend fun deleteExpense(id: String) =
        RetrofitInstance.api.deleteExpense(id)


    suspend fun monthGraph(date: Date, currency: Currency): Response<MonthGraphDTO> {
        val request = MonthGraphRequest(date, currency)

        return RetrofitInstance.api.monthGraph(request)
    }
}