package com.ait0ne.expensetracker.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ait0ne.expensetracker.R
import com.ait0ne.expensetracker.models.Category
import com.ait0ne.expensetracker.models.ExpenseDTO
import com.ait0ne.expensetracker.models.dto.CreateExpenseRequest
import com.ait0ne.expensetracker.repositories.CategoriesRepository
import com.ait0ne.expensetracker.repositories.ExpensesRepository
import com.ait0ne.expensetracker.utils.DateUtils
import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.*

class ExpenseViewModel(val expensesRepository: ExpensesRepository, val categoriesRepository: CategoriesRepository):ViewModel() {

    var expense: MutableLiveData<ExpenseDTO> = MutableLiveData()
    val options: MutableLiveData<List<String>> = MutableLiveData(listOf())
    val loading: MutableLiveData<Boolean> = MutableLiveData(false)
    val amountError: MutableLiveData<Int?> = MutableLiveData()
    val categoryError: MutableLiveData<Int?> = MutableLiveData()
    var errorCallback: ((error: Int) -> Unit)? = null
    var successCallback: ((success: Int) -> Unit)? = null
    var buttonCallback: (() -> Unit)? = null


    init {
        getCategoriesList()
    }



    fun setInitialExpense(initialExpense: ExpenseDTO) {
        expense.postValue(initialExpense)
    }

    fun updateExpense() {
        buttonCallback?.let {
            it()
        }

        if (loading.value != true) {

            val isValid = validate()

            if (isValid) {
                loading.postValue(!loading.value!!)

                createExpense()
            }

        }
    }


    private fun getCategoriesList() = viewModelScope.launch {


        val response = categoriesRepository.getCategories()

        val categories = handleCategoriesResponse(response)

        categories?.let {
            val new_options = it.map { c ->
                c.title
            }
            options.postValue(new_options)
        }
    }

    private fun handleCategoriesResponse(response: Response<MutableList<Category>>): MutableList<Category>? {

        if (response.isSuccessful) {
            response.body()?.let { result ->

                return result
            }


        }


        return null

    }


    private fun validate(): Boolean {
        var valid = true
        val state = expense.value
        if (state?.amount == 0f) {
            amountError.postValue(R.string.required)
            valid = false
        } else {
            amountError.postValue(null)
        }

        if (state?.category?.title.isNullOrEmpty()) {
            categoryError.postValue(R.string.required)
            valid = false
        } else {
            categoryError.postValue(null)
        }


        return valid
    }



    fun clearFieldError(field: FormFields) {
        when (field) {
            FormFields.AMOUNT -> {
                amountError.postValue(null)
            }
            FormFields.CATEGORY -> {
                categoryError.postValue(null)
            }
            FormFields.DESCRIPTION -> {

            }
        }
    }


    private fun createExpense() {
        viewModelScope.launch {


            val state = expense.value!!

            val response = expensesRepository.createExpense(
                CreateExpenseRequest(
                    category_name = state.category.title,
                    amount = state.amount,
                    date = state.date,
                    title = state.title,
                    id = state.id
                )
            )

            val expense = handleCreateExpenseResponse(response)

            if (expense == null) {
                errorCallback?.let {
                    it(R.string.addExpenseError)
                }
            } else {

                successCallback?.let {
                    it(R.string.addExpenseSuccess)
                }
            }

            loading.postValue(false)
        }
    }


    private fun handleCreateExpenseResponse(response: Response<ExpenseDTO>): ExpenseDTO? {

        if (response.isSuccessful) {
            response.body()?.let { result ->

                return result
            }


        }


        return null

    }




    fun changeDate(date: Date) {
        expense.postValue(expense.value!!.copy(date = date))
    }


    fun onError(callback: (error: Int) -> Unit) {
        errorCallback = callback
    }

    fun onSuccess(callback: (success: Int) -> Unit) {
        successCallback = callback
    }

    fun onButtonClick(callback: () -> Unit) {
        buttonCallback = callback
    }

}