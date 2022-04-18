package com.ait0ne.expensetracker.ui.viewmodels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ait0ne.expensetracker.models.Currency
import com.ait0ne.expensetracker.repositories.CategoriesRepository
import com.ait0ne.expensetracker.repositories.ExpensesRepository

class ExpensesListViewModelFactory(val expensesRepository: ExpensesRepository, val categoriesRepository: CategoriesRepository, val currency: Currency): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return  ExpensesListViewModel(expensesRepository, categoriesRepository, currency) as T
    }
}

