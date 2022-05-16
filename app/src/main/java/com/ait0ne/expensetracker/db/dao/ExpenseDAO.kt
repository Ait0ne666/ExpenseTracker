package com.ait0ne.expensetracker.db.dao

import android.content.ContentValues
import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import com.ait0ne.expensetracker.models.Category
import com.ait0ne.expensetracker.models.Expense
import com.ait0ne.expensetracker.models.ExpenseWithCategory
import kotlinx.coroutines.flow.Flow
import java.util.*


@Dao
interface  ExpenseDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(expense: Expense): Long


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSync(expense: Expense): Long



    @Query("select * from expenses WHERE  deleted_at is NULL")
    fun getAllExpensesForPeriod(): Flow<MutableList<ExpenseWithCategory>>

    @Query("select * from expenses where date>= :startMonth AND date <= :endMonth AND deleted_at is NULL order by date desc ")
     fun getAllExpensesForPeriod(startMonth: Long, endMonth: Long,): Flow<MutableList<ExpenseWithCategory>>

    @Query("select * from expenses where date>= :startMonth AND date <= :endMonth AND category_id  = LOWER(:category) AND deleted_at is NULL order by date desc")
     fun getAllExpensesForPeriod( startMonth: Long, endMonth: Long, category: Int): Flow<MutableList<ExpenseWithCategory>>





    @Query("select * from expenses")
    fun getCursorAll(): Cursor


    @Delete
    suspend fun deleteExpense(expense: Expense)







    @Query("update expenses set deleted_at = :deleted_at, dirty = :dirty where id = :expenseID")
    suspend fun deleteExpenseById(expenseID: Long, deleted_at: Date, dirty:Boolean = true): Int





    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCategory(category: Category): Long

    @Query("select * from categories where title = :title")
    suspend fun getCategoryByTitle(title: String): List<Category>


    @Query("select * from categories")
    fun getAllCategories(): Flow<MutableList<Category>>
}