package com.ait0ne.expensetracker.repositories

import com.ait0ne.expensetracker.api.RetrofitInstance

class CategoriesRepository(val RetrofitInstance:RetrofitInstance) {

    suspend fun getCategories() =
        RetrofitInstance.api.getCategories()
}