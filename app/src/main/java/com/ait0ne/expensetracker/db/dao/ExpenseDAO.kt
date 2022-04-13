package com.ait0ne.expensetracker.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ait0ne.expensetracker.models.Expense


@Dao
interface  ExpenseDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(expense: Expense): Long


    @Query("select * from expenses")
    fun getAllExpenses(): LiveData<List<Expense>>


    @Delete
    suspend fun deleteExpense(expense: Expense)

}