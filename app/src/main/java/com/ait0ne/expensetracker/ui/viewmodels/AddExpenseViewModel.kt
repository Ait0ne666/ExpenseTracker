package com.ait0ne.expensetracker.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ait0ne.expensetracker.R
import com.ait0ne.expensetracker.models.Category
import com.ait0ne.expensetracker.models.Currency
import com.ait0ne.expensetracker.models.Expense
import com.ait0ne.expensetracker.models.ExpenseDTO
import com.ait0ne.expensetracker.models.dto.CreateExpenseRequest
import com.ait0ne.expensetracker.models.dto.DayExpensesRequest
import com.ait0ne.expensetracker.models.dto.DayResponseDto
import com.ait0ne.expensetracker.repositories.CategoriesRepository
import com.ait0ne.expensetracker.repositories.ExpensesRepository
import com.ait0ne.expensetracker.repositories.LocalRepository
import com.ait0ne.expensetracker.ui.bottomsheetpicker.SelectOption
import com.ait0ne.expensetracker.utils.CurrencyUtils
import com.ait0ne.expensetracker.utils.DateUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.*





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
    private val categoriesRespository: CategoriesRepository,
    private val localRepository: LocalRepository
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
    var currency: MutableLiveData<Currency> = MutableLiveData(localRepository.getCurrency())
    val symbol: MutableLiveData<String>
        get() {
            return MutableLiveData(Currency.symbol(currency.value!!))
        }

    private var job = Job()
        get() {
            if (field.isCancelled) field = Job()
            return field
        }


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


        if (job.isActive) {
            job.cancel()
        }


        getTotalExpenses(currency.value)


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


        val categories = categoriesRespository.getCategories()


        categories.collect {
            val new_options = it.map { c ->
                c.title.capitalize()
            }
            options.postValue(new_options);
        };
    }


    private fun getTotalExpenses(cur: Currency?) = viewModelScope.launch(job) {
        val currency = cur ?: Currency.RUB
        val rates = localRepository.getRates()
        val expenses = expensesRepository.getExpensesForDay(Date())

        expenses.collect { expensesList ->

            var total = 0F

            expensesList.forEach {
                total += CurrencyUtils.convertCurrency(it.expense.amount, it.expense.currency, currency, rates)
            }


            day_total.postValue(total.toInt())
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


            val expenseToCreate = CreateExpenseRequest(
                category_name = state.category!!,
                amount = state.amount,
                date = state.date,
                title = state.description,
                currency = currency.value!!,
                id = null,
                created_at = null,
                cloud_id = null
            )

             expensesRepository.createExpenseFromDAO(
                expenseToCreate
            )

            resetFormState()



            successCallback?.let {
                it(R.string.addExpenseSuccess)
            }

            loading.postValue(false)
        }
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

    fun changeCurrency(cur: Currency) {
        localRepository.putCurrency(cur)
        getTotalExpenses(cur)
    }


    fun update() {
        if (job.isActive) {
            job.cancel()
        }


        getTotalExpenses(currency.value)
    }


}