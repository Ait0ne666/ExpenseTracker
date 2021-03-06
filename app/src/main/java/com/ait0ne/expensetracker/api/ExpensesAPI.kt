package com.ait0ne.expensetracker.api

import com.ait0ne.expensetracker.models.Category
import com.ait0ne.expensetracker.models.ExpenseDTO
import com.ait0ne.expensetracker.models.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ExpensesAPI {

    @POST("/login")
    suspend fun login(
    @Body
    body: LoginDTO
    ): Response<String>


    @GET("/categories")
    suspend fun getCategories(
    ): Response<MutableList<Category>>

    @POST("/expense")
    suspend fun createExpense(
        @Body
        body: CreateExpenseRequest,
    ): Response<ExpenseDTO>

    @POST("/expenses/day")
    suspend fun dayExpenses(
        @Body
        body: DayExpensesRequest,
    ): Response<DayResponseDto>


    @POST("/expenses/month")
    suspend fun monthExpenses(
        @Body
        body: MonthExpensesRequest,
    ): Response<MonthExpensesDTO>


    @DELETE("/expense/{id}")
    suspend fun deleteExpense(
        @Path("id") id:String
    ): Response<String>

    @POST("/expenses/category")
    suspend fun monthGraph(
        @Body
        body: MonthGraphRequest,
    ): Response<MonthGraphDTO>




    @POST("/sync")
    suspend fun sync(
        @Body
        body: SyncDTO
    ):Response<SyncDTO>
}

