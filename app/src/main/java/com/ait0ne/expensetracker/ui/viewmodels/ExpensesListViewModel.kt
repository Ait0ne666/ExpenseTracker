package com.ait0ne.expensetracker.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ait0ne.expensetracker.R
import com.ait0ne.expensetracker.models.Category
import com.ait0ne.expensetracker.models.Expense
import com.ait0ne.expensetracker.models.ExpenseDTO
import com.ait0ne.expensetracker.models.dto.DayExpensesRequest
import com.ait0ne.expensetracker.models.dto.MonthExpensesDTO
import com.ait0ne.expensetracker.repositories.CategoriesRepository
import com.ait0ne.expensetracker.repositories.ExpensesRepository
import com.ait0ne.expensetracker.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

val months = listOf("январь","февраль","март", "апрель","май","июнь","июль","август","сентябрь", "октябрь","ноябрь","декабрь",)

class ExpensesListViewModel(var expensesRepository: ExpensesRepository, var categoriesRepository: CategoriesRepository): ViewModel() {


    var expenses: MutableLiveData<Resource<MonthExpensesDTO>> = MutableLiveData()
    var options: MutableLiveData<List<String>> = MutableLiveData(arrayListOf("Все"))
    var categories: MutableList<Category> = mutableListOf()
    var selectedCategory:MutableLiveData<Int> = MutableLiveData(0)
    var selectedMonth: MutableLiveData<Date> = MutableLiveData(Date())

    var monthString: MutableLiveData<String> = MutableLiveData("Траты за " + months[selectedMonth.value?.month ?: 0])


    init {
        getCategoriesList()
        getTotalExpenses(selectedMonth.value!!, null);

    }

    private fun getTotalExpenses(date: Date, category: String?) = viewModelScope.launch {

        if (expenses.value == null ) {
            expenses.postValue(Resource.Loading())
        }


        val response = expensesRepository.monthExpenses(date, category)


        val result = handleExpensesResponse(response)


        expenses.postValue(result)
    }

    private fun getCategoriesList() = viewModelScope.launch {


        val response = categoriesRepository.getCategories()

        val cats = handleCategoriesResponse(response)

        cats?.let {
            var new_options = it.map { c ->
                c.title
            }
            var mutOptions = mutableListOf<String>()
            mutOptions.add("Все")
            mutOptions.addAll(1,new_options)
            categories = it
            options.postValue(mutOptions)
        }
    }


    private fun handleExpensesResponse(response: Response<MonthExpensesDTO>): Resource<MonthExpensesDTO> {

        if (response.isSuccessful) {
            response.body()?.let { result ->

                return Resource.Success(result)
            }


        }


        return Resource.Error("Неизвестная ошибка", data = expenses.value?.data)

    }


    private fun handleCategoriesResponse(response: Response<MutableList<Category>>): MutableList<Category>? {

        if (response.isSuccessful) {
            response.body()?.let { result ->

                return result
            }


        }


        return null

    }


    fun updateExpenses() {
        var category: String?

        if (selectedCategory.value == 0) {
            category = null
        } else {
            category = categories[selectedCategory.value!!-1].id
        }

        getTotalExpenses(selectedMonth.value!!, category)

    }


    fun updateExpenses(date: Date) {
        var category: String?

        if (selectedCategory.value == 0) {
            category = null
        } else {
            category = categories[selectedCategory.value!!-1].id
        }

        getTotalExpenses(date, category)

    }

    fun updateData() {
        var category: String?

        if (selectedCategory.value == 0) {
            category = null
        } else {
            category = categories[selectedCategory.value!!-1].id
        }

        getTotalExpenses(selectedMonth.value!!, category)

        getCategoriesList()
    }



    fun changeMonth(newDate: Date) {
        selectedMonth.postValue(newDate)
        monthString.postValue("Траты за " + months[newDate.month])
        updateExpenses(newDate)
    }



    fun deleteExpense(position: Int) = viewModelScope.launch {



        val currentDTO = expenses.value!!.data!!
        val expense = expenses.value!!.data!!.expenses
        val deleted = expense.removeAt(position)

        expenses.postValue(Resource.Success(currentDTO.copy(month_total = currentDTO.month_total - deleted.amount, expenses = expense)))

        val response = expensesRepository.deleteExpense(deleted.id)

        val result = handleDeleteExpenseResponse(response)

        when (result) {
            is Resource.Error -> {
                expenses.postValue(Resource.Success(currentDTO))
            }
            else -> {}
        }
    }

    fun handleDeleteExpenseResponse(response: Response<String>): Resource<String> {
        if (response.isSuccessful) {
            response.body()?.let { result ->

                return Resource.Success(result)
            }


        }


        return Resource.Error("Could not delete")
    }
}