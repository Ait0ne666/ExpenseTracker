package com.ait0ne.expensetracker.db.dao

import android.content.ContentValues
import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.*
import com.ait0ne.expensetracker.models.Expense


@Dao
interface  ExpenseDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(expense: Expense): Long


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSync(expense: Expense): Long


    @Query("select * from expenses")
    fun getAllExpenses(): LiveData<List<Expense>>


    @Query("select * from expenses")
    fun getCursorAll(): Cursor


    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("delete  from expenses where id= :expenseID")
    fun deleteExpenseByIdSync(expenseID: Long): Int


}