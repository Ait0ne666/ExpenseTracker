package com.ait0ne.expensetracker.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ait0ne.expensetracker.repositories.CategoriesRepository
import com.ait0ne.expensetracker.repositories.ExpensesRepository
import com.ait0ne.expensetracker.repositories.LocalRepository

class SharedViewModelFactory(val localRepository: LocalRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return  SharedViewModel(localRepository) as T
    }
}