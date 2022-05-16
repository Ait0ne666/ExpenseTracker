package com.ait0ne.expensetracker.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ait0ne.expensetracker.R
import com.ait0ne.expensetracker.models.*
import com.ait0ne.expensetracker.models.Currency
import com.ait0ne.expensetracker.models.dto.DayExpensesRequest
import com.ait0ne.expensetracker.models.dto.MonthExpensesDTO
import com.ait0ne.expensetracker.repositories.CategoriesRepository
import com.ait0ne.expensetracker.repositories.ExpensesRepository
import com.ait0ne.expensetracker.repositories.LocalRepository
import com.ait0ne.expensetracker.utils.CurrencyUtils
import com.ait0ne.expensetracker.utils.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlinx.coroutines.flow.collect

val months = listOf(
    "январь",
    "февраль",
    "март",
    "апрель",
    "май",
    "июнь",
    "июль",
    "август",
    "сентябрь",
    "октябрь",
    "ноябрь",
    "декабрь",
)

class ExpensesListViewModel(
    var expensesRepository: ExpensesRepository,
    var categoriesRepository: CategoriesRepository,
    private val localRepository: LocalRepository,
    private val currencyInit: Currency
) : ViewModel() {


    var expenses: MutableLiveData<Resource<MonthExpensesDTO>> = MutableLiveData()



    var options: MutableLiveData<List<String>> = MutableLiveData(arrayListOf("Все"))
    var categories: MutableList<Category> = mutableListOf()
    var selectedCategory: MutableLiveData<Int> = MutableLiveData(0)
    var selectedMonth: MutableLiveData<Date> = MutableLiveData(Date())

    var monthString: MutableLiveData<String> =
        MutableLiveData("Траты за " + months[selectedMonth.value?.month ?: 0])

    val currency: MutableLiveData<Currency> = MutableLiveData(currencyInit)
    private var job = Job()
        get() {
            if (field.isCancelled) field = Job()
            return field
        }

    init {


        getCategoriesList()
        getLocalExpenses(selectedMonth.value!!, null);

    }

    private fun getLocalExpenses(date: Date, category: Int?) {
        if (job.isActive) {
            job.cancel()
        }

        getExpenses(date, category)
    }


    private fun getExpenses(date: Date, category: Int?) = viewModelScope.launch(job) {
        if (expenses.value == null) {
            expenses.postValue(Resource.Loading())
        }


        val expensesLocal = expensesRepository.getExpensesForMonth(category, date)

        expensesLocal.collect { data ->
            var total = 0f
            val newExpenses = arrayListOf<ExpenseWithCategory>()
            val rates = localRepository.getRates()




            data.forEach {

                val currentAmount = if (it.expense.currency == currency.value) {
                    it.expense.amount
                } else {
                    CurrencyUtils.convertCurrency(
                        it.expense.amount,
                        it.expense.currency,
                        currency.value!!,
                        rates
                    )
                }


                total += currentAmount
                newExpenses.add(
                    ExpenseWithCategory(
                        expense = Expense(
                            id = it.expense.id!!,
                            title = it.expense.title,
                            category_id = it.category.id!!,
                            amount = currentAmount,
                            date = it.expense.date,
                            currency = currency.value!!,
                            created_at = it.expense.created_at,
                            updated_at = it.expense.updated_at,
                            deleted_at = it.expense.deleted_at,
                            cloud_id = it.expense.cloud_id,
                            dirty = it.expense.dirty

                        ),
                        category = Category(
                            id = it.category.id,
                            title = it.category.title.capitalize(),
                            cloud_id = it.category.cloud_id
                        )
                    )
                )
            }


            expenses.postValue(Resource.Success(MonthExpensesDTO(total, newExpenses)))

        }


    }


    private fun getCategoriesList() = viewModelScope.launch {


        val cats = categoriesRepository.getCategories()


        cats?.collect {
            var new_options = it.map { c ->
                c.title.capitalize()
            }
            var mutOptions = mutableListOf<String>()
            mutOptions.add("Все")
            mutOptions.addAll(1, new_options)
            categories = it
            options.postValue(mutOptions)
        }
    }


    fun updateExpenses() {
        var category: Int?

        if (selectedCategory.value == 0) {
            category = null
        } else {
            category = categories[selectedCategory.value!! - 1].id
        }

        getLocalExpenses(selectedMonth.value!!, category)

    }


    fun updateExpenses(date: Date) {
        var category: Int?

        if (selectedCategory.value == 0) {
            category = null
        } else {
            category = categories[selectedCategory.value!! - 1].id
        }

        getLocalExpenses(date, category)

    }



    fun changeMonth(newDate: Date) {
        selectedMonth.postValue(newDate)
        monthString.postValue("Траты за " + months[newDate.month])
        updateExpenses(newDate)
    }


    fun deleteExpense(position: Int) = viewModelScope.launch {

        val expenseToDelete = expenses.value!!.data!!.expenses[position]


        expensesRepository.deleteByID(expenseToDelete.expense.id!!)
    }


}