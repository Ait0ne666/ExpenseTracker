package com.ait0ne.expensetracker.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ait0ne.expensetracker.db.dao.ExpenseDAO
import com.ait0ne.expensetracker.models.Category
import com.ait0ne.expensetracker.models.Expense


@Database(
    entities = [Expense::class, Category::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class ExpenseDB: RoomDatabase() {

    abstract fun getExpenseDao(): ExpenseDAO


    companion object {

        @Volatile
        private var instance: ExpenseDB? = null

        private val LOCK = Any()


        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also { instance = it }
        }


        private fun createDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            ExpenseDB::class.java,
            "expense_db_db"
        )
            .build()
    }
}