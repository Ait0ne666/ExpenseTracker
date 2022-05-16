package com.ait0ne.expensetracker.repositories

import com.ait0ne.expensetracker.api.RetrofitInstance
import com.ait0ne.expensetracker.db.ExpenseDB

class CategoriesRepository( val db: ExpenseDB) {

    fun getCategories() =
        db.getExpenseDao().getAllCategories()
}