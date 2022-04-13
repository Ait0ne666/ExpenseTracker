package com.ait0ne.expensetracker.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ait0ne.expensetracker.R
import com.ait0ne.expensetracker.models.Category
import com.ait0ne.expensetracker.models.Expense
import com.ait0ne.expensetracker.models.ExpenseDTO
import com.ait0ne.expensetracker.models.dto.CreateExpenseRequest
import com.ait0ne.expensetracker.models.dto.DayExpensesRequest
import com.ait0ne.expensetracker.models.dto.DayResponseDto
import com.ait0ne.expensetracker.repositories.CategoriesRepository
import com.ait0ne.expensetracker.repositories.ExpensesRepository
import com.ait0ne.expensetracker.ui.bottomsheetpicker.SelectOption
import com.ait0ne.expensetracker.utils.DateUtils
import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.*


class InitialFormState(val amount: Float, val description: String, val category: String?) {

}


data class AddExpenseFormState(
    var amount: Float,
    var description: String,
    var category: String?,
    var date: Date
) {

}


enum class FormFields {
    AMOUNT,
    DESCRIPTION,
    CATEGORY,
    DATE
}


class AddExpenseViewModel(
    private val expensesRepository: ExpensesRepository,
    private val categoriesRespository: CategoriesRepository
) : ViewModel() {


    val options: MutableLiveData<List<String>> = MutableLiveData(listOf())
    val addExpenseFormState: MutableLiveData<AddExpenseFormState> = MutableLiveData()
    val loading: MutableLiveData<Boolean> = MutableLiveData(false)
    val amountError: MutableLiveData<Int?> = MutableLiveData()
    val categoryError: MutableLiveData<Int?> = MutableLiveData()
    var errorCallback: ((error: Int) -> Unit)? = null
    var successCallback: ((success: Int) -> Unit)? = null
    var buttonCallback: (() -> Unit)? = null
    var day_total: MutableLiveData<Int> = MutableLiveData(0)
    var month_total: MutableLiveData<Int> = MutableLiveData(0)


    init {
        getCategoriesList()
        addExpenseFormState.postValue(
            AddExpenseFormState(
                amount = 0f,
                description = "",
                category = null,
                date = Date()

            )
        )
    }

    fun addExpense(

    ) {

        buttonCallback?.let {
            it()
        }

        if (loading.value != true) {
            val currentState = addExpenseFormState

            val isValid = validateFormState(currentState.value)

            if (isValid) {
                loading.postValue(!loading.value!!)

                createExpense(currentState.value!!)
            }

        }

    }


    private fun getCategoriesList() = viewModelScope.launch {


        val response = categoriesRespository.getCategories()

        val categories = handleCategoriesResponse(response)

        categories?.let {
            val new_options = it.map { c ->
                c.title
            }
            options.postValue(new_options)
        }
    }


    private fun getTotalExpenses() = viewModelScope.launch {


        val response = expensesRepository.dayExpenses(DayExpensesRequest(Date()))


        handleDayExpensesResponse(response)

    }


    private fun handleCategoriesResponse(response: Response<MutableList<Category>>): MutableList<Category>? {

        if (response.isSuccessful) {
            response.body()?.let { result ->

                return result
            }


        }


        return null

    }


    private fun handleDayExpensesResponse(response: Response<DayResponseDto>) {

        if (response.isSuccessful) {
            response.body()?.let { result ->

                day_total.postValue(result.day_total.toInt())
                month_total.postValue(result.month_total.toInt())
            }


        }


    }


    private fun validateFormState(state: AddExpenseFormState?): Boolean {
        var valid = true
        if (state?.amount == 0f) {
            amountError.postValue(R.string.required)
            valid = false
        } else {
            amountError.postValue(null)
        }

        if (state?.category.isNullOrEmpty()) {
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


    private fun createExpense(state: AddExpenseFormState) {
        viewModelScope.launch {


            val response = expensesRepository.createExpense(
                CreateExpenseRequest(
                    category_name = state.category!!,
                    amount = state.amount,
                    date = state.date,
                    title = state.description,
                    id = null
                )
            )

            val expense = handleCreateExpenseResponse(response)

            if (expense == null) {
                errorCallback?.let {
                    it(R.string.addExpenseError)
                }
            } else {

                resetFormState()

                if (DateUtils.isSameDay(expense.date, Date())) {
                    day_total.postValue(expense.amount.toInt() + (day_total.value?:0))
                    month_total.postValue(expense.amount.toInt() + (month_total.value?:0))
                }


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


    private fun resetFormState() {
        addExpenseFormState.postValue(
            AddExpenseFormState(
                amount = 0f,
                description = "",
                category = null,
                date = Date()
            )
        )

    }


    fun changeDate(date: Date) {
        addExpenseFormState.postValue(addExpenseFormState.value!!.copy(date = date))
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



    fun updateTotal() {
        getTotalExpenses()
    }
}